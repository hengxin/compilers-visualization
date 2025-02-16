package hmb.spring.serviceimpl;

import hmb.antlr4.trans.AtnCreator;
import hmb.antlr4.trans.EditTreeListener;
import hmb.antlr4.trans.ToTreeNodeUtils;
import hmb.protobuf.Request.*;
import hmb.protobuf.Response;
import hmb.protobuf.Response.*;
import hmb.spring.config.MyServiceException;
import hmb.utils.clazz.ObjectManager;
import hmb.utils.tools.OperationCreator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.EmptyPredictionContext;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Service
public class ParseService {


    private final static Logger logger = LoggerFactory.getLogger(ParseService.class);

    private final String DATABASE_PATH;
    private final static String ANTLR = "antlr4";

    public ParseService(@Value("${MyProjectPath:}") String projectDir) {
        DATABASE_PATH = projectDir + "src/main/usr_src/";
    }


    private static void mkdir(String path) {
        File f = new File(path);
        if (!f.isDirectory()) {
            boolean success = f.mkdirs();
            if (!success && !f.isDirectory()) {
                logger.error(path + " is not dir");
                throw new MyServiceException(path + " is not dir");
            }
        }
    }

    private static void addListeners(ATNSimulator atnSimulator, MainResponse.Builder mainResponseBuilder, Map<ATNState, ATNState> mapper) {
        atnSimulator.setStartStateClosureListener(dfaState -> {
            var operation = StartStateClosureOperation
                    .newBuilder()
                    .setStartingClosure(OperationCreator.makeDFAState(dfaState, mapper))
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
        atnSimulator.setReachImmediateListener((atnStates, isUnique) -> {
            var builder = ReachImmediateOperation.newBuilder().setIsUnique(isUnique);
            atnStates.forEach(state -> builder.addReached(OperationCreator.makeATNState(state, mapper)));
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(builder.build()));
        });
        atnSimulator.setCalEpsilonClosureListener(atnStates -> {
            var builder = StartCalEpsilonClosureOperation.newBuilder();
            atnStates.forEach(state -> builder.addStart(OperationCreator.makeATNState(state, mapper)));
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(builder.build()));
        });
        atnSimulator.setCalEpsilonClosureFinishListener(pairs -> {
            var builder = CalEpsilonClosureFinishOperation.newBuilder();
            for (Pair<ATNState, ATNState> p : pairs) {
                builder.addEpsilonEdge(PairAtnStateMsg.newBuilder()
                        .setFirst(OperationCreator.makeATNState(p.a, mapper))
                        .setSecond(OperationCreator.makeATNState(p.b, mapper))
                        .build());
            }
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(builder.build()));
        });
        atnSimulator.setAddNewDFAStateListener((dfaState, isNew) -> {
            var operation = ReachDFAStateOperation
                    .newBuilder()
                    .setDfaState(OperationCreator.makeDFAState(dfaState, mapper))
                    .setIsNew(isNew)
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
        atnSimulator.setAddNewEdgeListener((from, to, upon) -> {
            var operation = AddNewEdgeOperation
                    .newBuilder()
                    .setNewEdge(EdgeMsg.newBuilder()
                            .setFrom(OperationCreator.makeDFAState(from, mapper))
                            .setTo(OperationCreator.makeDFAState(to, mapper))
                            .setUpon(upon)
                            .build())
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
        atnSimulator.setReuseStateListener((from, to, upon) -> {
            var operation = ReuseEdgeOperation
                    .newBuilder()
                    .setReuse(EdgeMsg.newBuilder()
                            .setFrom(OperationCreator.makeDFAState(from, mapper))
                            .setTo(OperationCreator.makeDFAState(to, mapper))
                            .setUpon(upon)
                            .build())
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
        atnSimulator.setSwitchTableListener((dfaStateList, edgeList, startAtn, decision) -> {
            var operation = StartAdaptiveOperation
                    .newBuilder()
                    .addAllDfaStates(dfaStateList.stream().map(
                            s -> OperationCreator.makeDFAState(s, mapper)
                    ).toList())
                    .addAllEdges(edgeList.stream().map(
                            e -> EdgeMsg.newBuilder()
                                    .setFrom(OperationCreator.makeDFAState(e.a, mapper))
                                    .setTo(OperationCreator.makeDFAState(e.b, mapper))
                                    .setUpon(e.c)
                                    .build()
                    ).toList())
                    .setStartAtn(OperationCreator.makeATNState(startAtn, mapper))
                    .setDecision(decision)
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
        atnSimulator.setEndAdaptiveListener(alt -> {
            var operation = EndAdaptiveOperation.newBuilder()
                    .setAlt(alt)
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
        atnSimulator.setStayAtDFAStateListener(dfaState -> {
            var operation = StayAtDFAStateOperation.newBuilder()
                    .setDfaState(OperationCreator.makeDFAState(dfaState, mapper))
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
    }

    private static void addListeners(Parser parser, MainResponse.Builder mainResponseBuilder, ToTreeNodeUtils toTreeNodeUtils) {
        parser.setConsumeTokenListener((token, node) -> {
            var operation = ConsumeTokenOperation.newBuilder()
                    .setTokenConsumed(TokenMsg.newBuilder()
                            .setTokenType(token.getType())
                            .setTokenRule(toTreeNodeUtils.lexerVocabulary().getSymbolicName(token.getType()))
                            .setTokenText(token.getText())
                            .setChannel(token.getChannel())
                            .build())
                    .setParserState(Response.ParserState.newBuilder().setRoot(toTreeNodeUtils.toTree(node, node.getParent())))
                    .build();
            mainResponseBuilder.addOperation(OperationCreator.makeOperation(operation));
        });
    }

    public MainResponse parse(MainRequest mainRequest) throws IOException {
        final String user = "u_" + mainRequest.getUserId();
        final String userDir = DATABASE_PATH + user + "/";
        final String codeDir = userDir + ANTLR + "/";
        mkdir(codeDir);
        final String packageName = user + "." + ANTLR;
        final String grammarName = mainRequest.getName();
        final String lexerFileName = codeDir + (grammarName + "Lexer.g4");
        final String parserFileName = codeDir + (grammarName + "Parser.g4");

        {
            //  保存用户的两个.g4文件
            FileWriter fileWriter = new FileWriter(lexerFileName);
            fileWriter.write(mainRequest.getLexer());
            fileWriter.close();
            fileWriter = new FileWriter(parserFileName);
            fileWriter.write(mainRequest.getParser());
            fileWriter.close();
        }

        {
            //  根据.g4文件生成java的lexer、parser框架代码
            org.antlr.v4.Tool tool = new org.antlr.v4.Tool(new String[]{
                    "-o", codeDir,
                    "-package", packageName,
                    parserFileName,
                    lexerFileName
            });
            PrintStream S_ERR = System.err;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            System.setErr(ps);
            tool.processGrammarsOnCommandLine();
            System.setErr(S_ERR);
            if (tool.errMgr.getNumErrors() > 0) {
                String err = baos.toString().replaceAll(codeDir, "");
                throw new MyServiceException("[parse g4 error]: \n" + err);
            }
        }


        {
            //  编译生成.class
            JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
            try (StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(
                    null,
                    Locale.CHINA,
                    StandardCharsets.UTF_8
            )) {
                Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjects(
                        new File(codeDir + grammarName + "Lexer.java"),
                        new File(codeDir + grammarName + "Parser.java"),
                        new File(codeDir + grammarName + "ParserBaseListener.java"),
                        new File(codeDir + grammarName + "ParserListener.java"));
                JavaCompiler.CompilationTask task = javaCompiler.getTask(
                        null,
                        fileManager,
                        null,
                        null,
                        null,
                        units
                );
                task.call();
            }
        }


        //  用自定义类的加载器加载lexer和parser，并作处理
        try (ObjectManager<Lexer> lexer = new ObjectManager<>(
                new URL("file:" + DATABASE_PATH),
                packageName + "." + grammarName + "Lexer",
                new Class[]{CharStream.class},
                CharStreams.fromString(mainRequest.getCode())
        )) {
            final MainResponse.Builder mainResponseBuilder = MainResponse.newBuilder();


            List<? extends Token> allTokens = lexer.get().getAllTokens();
            lexer.get().reset();  // 记得恢复

            for (Token token : allTokens) {
                if (token.getChannel() == Token.DEFAULT_CHANNEL) {
                    mainResponseBuilder.addToken(TokenMsg.newBuilder()
                            .setTokenType(token.getType())
                            .setTokenRule(lexer.get().getVocabulary().getSymbolicName(token.getType()))
                            .setTokenText(token.getText())
                            .setChannel(token.getChannel())
                            .build());
                }
            }

            CommonTokenStream tokens = new CommonTokenStream(lexer.get());
            ObjectManager<Parser> parser = new ObjectManager<>(
                    lexer,
                    packageName + "." + grammarName + "Parser",
                    new Class[]{TokenStream.class},
                    tokens
            );
            // lexer与parser加载完毕

//            parser.get().setTrace(true);
            var lexerAtnCreator = new AtnCreator(lexer.get());
            var parserAtnCreator = new AtnCreator(parser.get(), lexer.get().getVocabulary());
            var lexerATN = lexerAtnCreator.getATNs();
            var lexerMapper = lexerAtnCreator.getMapper();
            var parserATN = parserAtnCreator.getATNs();
            var parserMapper = parserAtnCreator.getMapper();

            ToTreeNodeUtils toTreeNodeUtils = new ToTreeNodeUtils(lexer.get().getVocabulary(), parser.get().getRuleNames());

            // 添加parser监听
            addListeners(parser.get().getInterpreter(), mainResponseBuilder, parserMapper);
            addListeners(parser.get(), mainResponseBuilder, toTreeNodeUtils);

            // 生成树
            parser.get().addParseListener(new EditTreeListener(mainResponseBuilder, toTreeNodeUtils));


            StringBuilder errorSB = new StringBuilder();
            parser.get().removeErrorListeners();
            parser.get().addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line,
                                        int charPositionInLine,
                                        String msg,
                                        RecognitionException e)
                {
                    errorSB.append("line ").append(line).append(':').append(charPositionInLine).append(' ').append(msg).append(".\n");
                }
            });
            final String startRule = Strings.isBlank(mainRequest.getStartRule()) ? "program" : mainRequest.getStartRule().trim();
            try {
                ParserRuleContext program = parser.invokeMemberMethod(startRule, new Class[0]);
            } catch (NoSuchMethodException e) {
                throw new MyServiceException("NoSuchRule: " + startRule);
            }
            if (!errorSB.isEmpty()) {
                throw new MyServiceException("[SyntaxError]\n\n" + errorSB);
            }

            return mainResponseBuilder.setSuccess(true).setInitialState(InitialState.newBuilder()
                    .setLexerATN(lexerATN)
                    .setParserATN(parserATN)
                    .build()
            ).build();
        }


    }
}

package hmb.antlr4.trans;


import hmb.protobuf.Response.ParserResult;
import hmb.protobuf.Response.ParserState;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

public class GenTreeParseTreeListener implements ParseTreeListener {

    private final ParserResult.Builder parserResult;
    private final ToTreeNodeUtils toTreeNodeUtils;

    public GenTreeParseTreeListener(ParserResult.Builder parserResult, Vocabulary lexerVocabulary, String[] parserRuleNames) {
        this.parserResult = parserResult;
        this.toTreeNodeUtils = new ToTreeNodeUtils(lexerVocabulary, parserRuleNames);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        parserResult.addParserState(ParserState.newBuilder().setRoot(toTreeNodeUtils.toTree(node, node)));
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        parserResult.addParserState(ParserState.newBuilder().setRoot(toTreeNodeUtils.toTree(ctx, ctx)));
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        parserResult.addParserState(ParserState.newBuilder().setRoot(toTreeNodeUtils.toTree(ctx, ctx.parent)));
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        System.err.println("visit ErrorNode");
    }

}

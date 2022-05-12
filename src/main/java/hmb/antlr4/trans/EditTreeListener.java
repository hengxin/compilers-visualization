package hmb.antlr4.trans;

import hmb.protobuf.Response;
import hmb.utils.tools.OperationCreator;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;

public class EditTreeListener implements ParseTreeListener {

    private final Response.MainResponse.Builder builder;
    private final ToTreeNodeUtils toTreeNodeUtils;

    public EditTreeListener(Response.MainResponse.Builder builder, ToTreeNodeUtils toTreeNodeUtils) {
        this.builder = builder;
        this.toTreeNodeUtils = toTreeNodeUtils;
    }

    public EditTreeListener(Response.MainResponse.Builder builder, Vocabulary lexerVocabulary, String[] parserRuleNames) {
        this.builder = builder;
        this.toTreeNodeUtils = new ToTreeNodeUtils(lexerVocabulary, parserRuleNames);
    }


    @Override
    public void visitErrorNode(ErrorNode node) {
        throw new RuntimeException("ErrorNode = " + node.toStringTree());
    }

    @Override
    public void visitTerminal(TerminalNode node) {
//        啥都不用干，consume Token 已经帮忙搞定了
//
//        builder.addOperation(
//                OperationCreator.makeOperation(Response.EditTreeOperation.newBuilder()
//                        .setParserState(Response.ParserState.newBuilder().setRoot(toTreeNodeUtils.toTree(node, node)))
//                        .setType("")
//                        .build())
//        );
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        builder.addOperation(
                OperationCreator.makeOperation(Response.EditTreeOperation.newBuilder()
                        .setParserState(Response.ParserState.newBuilder().setRoot(toTreeNodeUtils.toTree(ctx, ctx)))
                        .setType("添加新的树节点 " + toTreeNodeUtils.parserRuleNames()[ctx.getRuleIndex()])
                        .build())
        );
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        builder.addOperation(
                OperationCreator.makeOperation(Response.EditTreeOperation.newBuilder()
                        .setParserState(Response.ParserState.newBuilder().setRoot(toTreeNodeUtils.toTree(ctx, ctx.parent)))
                        .setType("离开当前树节点 " + toTreeNodeUtils.parserRuleNames()[ctx.getRuleIndex()])
                        .build())
        );
    }
}

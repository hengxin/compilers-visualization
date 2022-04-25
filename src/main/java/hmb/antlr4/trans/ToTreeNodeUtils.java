package hmb.antlr4.trans;

import hmb.protobuf.Response.TreeNode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ToTreeNodeUtils {

    private final Vocabulary lexerVocabulary;
    private final String[] parserRuleNames;

    public ToTreeNodeUtils(Vocabulary lexerVocabulary, String[] parserRuleNames) {
        this.lexerVocabulary = lexerVocabulary;
        this.parserRuleNames = parserRuleNames;
    }


    public TreeNode toTree(ParseTree parseTree) {
        return doToTree(findRoot(parseTree));
    }

    private static ParseTree findRoot(ParseTree parseTree) {
        while (true) {
            ParseTree parent = parseTree.getParent();
            if (parent == null) {
                return parseTree;
            } else {
                parseTree = parent;
            }
        }
    }

    private TreeNode doToTree(ParseTree parseTree) {
        TreeNode.Builder result = TreeNode.newBuilder();
        switch (parseTree) {
            case ParserRuleContext parserRuleContext -> result.setName(this.getName(parserRuleContext));
            case TerminalNode terminalNode -> result.setName(this.getName(terminalNode));
            case null -> throw new NullPointerException("ParseTree");
            default -> {
                result.setName("unknown");
                System.err.println("Unknown parseTree type: " + parseTree.getClass().getName());
            }
        }

        final int SIZE = parseTree.getChildCount();
        if (SIZE != 0) {
            for (int i = 0; i < SIZE; i++) {
                result.addChildren(doToTree(parseTree.getChild(i)));
            }
        }
        return result.build();
    }

    private String getName(ParserRuleContext ctx) {
        return parserRuleNames[ctx.getRuleIndex()];
    }

    private String getName(TerminalNode node) {
        return lexerVocabulary.getSymbolicName(node.getSymbol().getType());
    }
}

package hmb.antlr4.trans;

import hmb.protobuf.Response;
import hmb.protobuf.Response.TreeNode;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.awt.*;

public record ToTreeNodeUtils(Vocabulary lexerVocabulary, String[] parserRuleNames) {

    private static String makeColorString(Color c) {
        return String.format("rgb(%d,%d,%d,%f)", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha() / 255.0d);
    }

    private static final Color highlightColor = new Color(8, 32, 192);
    private static final String highlightColorString = makeColorString(highlightColor);
    private static final Color leafColor = new Color(0, 255, 0, 64);
    private static final String leafColorString = makeColorString(leafColor);
    private static final Color branchColor = new Color(224, 192, 0, 64);
    private static final String branchColorString = makeColorString(branchColor);


    public TreeNode toTree(ParseTree parseTree, ParseTree highlight) {
        return doToTree(findRoot(parseTree), highlight);
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

    private TreeNode doToTree(ParseTree parseTree, final ParseTree highlight) {
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
        Response.ItemStyle.Builder itemStyle = Response.ItemStyle.newBuilder();
        switch (parseTree) {
            case ParserRuleContext ignored -> itemStyle.setColor(branchColorString);
            case TerminalNode ignored -> itemStyle.setColor(leafColorString);
            default -> {
            }
        }
        if (parseTree == highlight) {
            itemStyle.setColor(highlightColorString);
        }
        result.setItemStyle(itemStyle);

        final int SIZE = parseTree.getChildCount();
        if (SIZE != 0) {
            for (int i = 0; i < SIZE; i++) {
                result.addChildren(doToTree(parseTree.getChild(i), highlight));
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

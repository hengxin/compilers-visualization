import { createStore } from "vuex";

const myStore = createStore({
  state() {
    return {
      userId: 0,
      globalGrammarName: "Cmm",
      globalLexerString:
        "lexer grammar CmmLexer;\n" +
        "\n" +
        "\n" +
        "Whitespace\n" +
        "    :   [ \\t]+\n" +
        "        -> channel(HIDDEN)\n" +
        "    ;" +
        "\n" +
        "Newline\n" +
        "    :   (   '\\r' '\\n'?\n" +
        "        |   '\\n'\n" +
        "        )\n" +
        "        -> channel(HIDDEN)\n" +
        "    ;\n" +
        "\n" +
        "\n" +
        "\n" +
        "TYPE : 'int' | 'float';\n" +
        "\n" +
        "LP : '(';\n" +
        "RP : ')';\n" +
        "LB : '[';\n" +
        "RB : ']';\n" +
        "LC : '{';\n" +
        "RC : '}';\n" +
        "\n" +
        "RELOP : '<' | '<=' | '>' | '>=' | '==' | '!=';\n" +
        "\n" +
        "PLUS : '+';\n" +
        "MINUS : '-';\n" +
        "STAR : '*';\n" +
        "DIV : '/';\n" +
        "\n" +
        "\n" +
        "SEMI : ';';\n" +
        "COMMA : ',';\n" +
        "\n" +
        "ASSIGNOP : '=';\n" +
        "\n" +
        "DOT : '.';\n" +
        "\n" +
        "\n" +
        "\n" +
        "ID : Nondigit (Nondigit | Digit)*;\n" +
        "\n" +
        "INT : NonZeroDigit Digit*;\n" +
        "\n" +
        "FLOAT : Science | Real;\n" +
        "\n" +
        "\n" +
        "fragment\n" +
        "Nondigit\n" +
        "    :   [a-zA-Z_]\n" +
        "    ;\n" +
        "\n" +
        "fragment\n" +
        "Digit\n" +
        "    :   [0-9]\n" +
        "    ;\n" +
        "\n" +
        "fragment\n" +
        "NonZeroDigit\n" +
        "    :   [1-9]\n" +
        "    ;\n" +
        "\n" +
        "\n" +
        "fragment\n" +
        "Real\n" +
        "    :   Digit+ DOT Digit+\n" +
        "    ;\n" +
        "\n" +
        "fragment\n" +
        "Science\n" +
        "    :   ((Digit* DOT Digit+)|(Digit+ DOT Digit*)) ('e'|'E') (('+'|'-')?) Digit+\n" +
        "    ;",
      globalParserString:
        "parser grammar CmmParser;\n" +
        "\n" +
        "options {\n" +
        "  tokenVocab=CmmLexer;\n" +
        "}\n" +
        "\n" +
        "program: stmt+ ;\n" +
        "\n" +
        "stmt:\n" +
        "    expr1 SEMI |\n" +
        "    expr2 SEMI |\n" +
        "    expr3 SEMI ;\n" +
        "\n" +
        "expr1:\n" +
        "    INT |\n" +
        "    LP expr1 RP;\n" +
        "\n" +
        "expr2:\n" +
        "    FLOAT |\n" +
        "    LP expr2 RP;\n" +
        "\n" +
        "expr3:\n" +
        "    LP* ID RP* ;\n",
      globalCodeString: "((2));\n" + "(((3.3)));",
      globalStartRule: "program"
    };
  },
  mutations: {
    setUserId(state, userId) {
      state.userId = userId;
    },
    setGrammarString(state, grammarName) {
      state.globalGrammarName = grammarName;
    },
    setLexer(state, lexer) {
      state.globalLexerString = lexer;
    },
    setParser(state, parser) {
      state.globalParserString = parser;
    },
    setCode(state, code) {
      state.globalCodeString = code;
    },
    setStartRule(state, rule) {
      state.globalStartRule = rule;
    }
  }
});

export default myStore;

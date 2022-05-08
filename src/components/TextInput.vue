<template>
  <div style="padding-left: 10px; padding-right: 10px">
    <a-input-group compact>
      <a-input
        v-model:value="grammarName"
        placeholder="grammar name"
        style="width: 30%"
      />
      <a-button type="primary" @click="ok">开始解析</a-button>
      <a-button
        type="default"
        @click="
          () => {
            this.grammarName = '';
            this.lexer = '';
            this.parser = '';
            this.code = '';
          }
        "
        danger
        >clear</a-button
      >
    </a-input-group>
    <br />
    <a-row>
      <a-col :span="11">
        <a-textarea
          v-model:value="lexer"
          placeholder="g4 lexer grammar"
          :rows="13"
        />
        <a-textarea
          v-model:value="parser"
          placeholder="g4 parser grammar"
          :rows="13"
        />
      </a-col>
      <a-col span="2" />
      <a-col :span="11">
        <a-textarea v-model:value="code" placeholder="code" :rows="27" />
      </a-col>
    </a-row>
  </div>
</template>

<script>
import {
  Button,
  Col,
  Input,
  InputGroup,
  message,
  Row,
  Textarea
} from "ant-design-vue";
import { mapMutations, mapState } from "vuex";
import { parse } from "@/data/parse";
import { showAll } from "@/router";

export default {
  name: "TextInput",
  components: {
    AButton: Button,
    ACol: Col,
    ARow: Row,
    AInput: Input,
    AInputGroup: InputGroup,
    ATextarea: Textarea
  },
  data() {
    return {
      grammarName: "Cmm",
      lexer:
        "lexer grammar CmmLexer;\n" +
        "\n" +
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
      parser:
        "parser grammar CmmParser;\n" +
        "\n" +
        "options {\n" +
        "  tokenVocab=CmmLexer;\n" +
        "}\n" +
        "\n" +
        "program: statement*  ( LC statement* RC )* ;\n" +
        "\n" +
        "statement:\n" +
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
        "    LP expr2 RP RP;\n" +
        "\n" +
        "expr3:\n" +
        "    LP* ID RP* ;\n" +
        "\n" +
        "expr4: expr4 PLUS expr4 |expr4 STAR expr4| ID | FLOAT;",
      code: ""
    };
  },
  computed: {
    ...mapState(["userId"])
  },
  methods: {
    ...mapMutations(["setUserId"]),
    ok() {
      this.setUserId(0);
      const userId = this.userId;
      const grammarName = this.grammarName;
      const lexer = this.lexer;
      const parser = this.parser;
      const code = this.code;
      parse(userId, grammarName, lexer, parser, code)
        .then(s => {
          if (s) {
            showAll();
          }
        })
        .catch(e => {
          message.warn("Uncaught Error");
          throw e;
        });
    }
  }
};
</script>

<style scoped></style>

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
      <a-col :span="2" />
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
    return {};
  },
  computed: {
    ...mapState([
      "userId",
      "globalGrammarName",
      "globalLexerString",
      "globalParserString",
      "globalCodeString"
    ]),
    grammarName: {
      get() {
        return this.globalGrammarName;
      },
      set(val) {
        this.setGrammarString(val);
      }
    },
    lexer: {
      get() {
        return this.globalLexerString;
      },
      set(val) {
        this.setLexer(val);
      }
    },
    parser: {
      get() {
        return this.globalParserString;
      },
      set(val) {
        this.setParser(val);
      }
    },
    code: {
      get() {
        return this.globalCodeString;
      },
      set(val) {
        this.setCode(val);
      }
    }
  },
  methods: {
    ...mapMutations([
      "setUserId",
      "setGrammarString",
      "setLexer",
      "setParser",
      "setCode"
    ]),
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
          if (e instanceof Error && e.message === "Network Error") {
            message.error("NetworkError");
          } else {
            message.warn("Uncaught Error");
          }
          throw e;
        });
    }
  }
};
</script>

<style scoped></style>

<template>
  <div style="padding: 10px">
    <a-input-group compact>
      <a-input
        v-model:value="name"
        placeholder="grammar name"
        style="width: 30%"
      />
      <a-button type="primary" @click="ok">ok</a-button>
    </a-input-group>
    <br />
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
import { Button, Row, Col, Input, InputGroup, Textarea } from "ant-design-vue";
import { mapState, mapMutations } from "vuex";
import parse from "../data/parse";

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
      name: "",
      lexer: "",
      parser: "",
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
      const name = this.name;
      const lexer = this.lexer;
      const parser = this.parser;
      const code = this.code;
      parse(userId, name, lexer, parser, code);
    }
  }
};
</script>

<style scoped></style>

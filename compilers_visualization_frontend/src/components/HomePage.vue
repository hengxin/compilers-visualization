<template>
  <h1>Home</h1>
  <p>
    <router-link to="/hello">HelloWorld</router-link>
  </p>
  <p>
    <router-link to="/input">InputGrammarAndCode</router-link>
  </p>
  <p>
    <router-link to="/parser">Parser</router-link>
  </p>
  <p>
    <router-link to="/tree">Tree</router-link>
  </p>
</template>

<script>
export default {
  name: "HomePage",
  components: {}
};
</script>

<style scoped></style>

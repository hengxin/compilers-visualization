import { createApp } from "vue";

import App from "./App.vue";

import "ant-design-vue/dist/antd.css";
import "ant-design-vue";
import "echarts";

import myRouter from "./router";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import myStore from "./store.js";

const app = createApp(App);
app.use(myRouter);
app.use(myStore);

app.mount("#app");

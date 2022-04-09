import { createApp } from "vue";
import App from "./App.vue";

import "ant-design-vue/dist/antd.css";
import "ant-design-vue";
import "echarts";

import myRouter from "./router.js";

const app = createApp(App);
app.use(myRouter);
app.mount("#app");

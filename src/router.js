import { createRouter, createWebHashHistory } from "vue-router";

const routes = [
  { path: "/", component: () => import("./components/HomePage.vue") },
  { path: "/input", component: () => import("./components/TextInput.vue") },
  { path: "/hello", component: () => import("./components/HelloWorld.vue") },
  { path: "/parser", component: () => import("./components/ShowParser.vue") },
  { path: "/tree", component: () => import("./components/EchartsTree") },
  { path: "/demo", component: () => import("./_demo/Demo") },
  { path: "/all", component: () => import("./components/ShowAll") },

  // Final
  {
    path: "/:pathMatch(.*)*",
    component: () => import("./components/FourZeroFour")
  }
];

const myRouter = createRouter({
  history: createWebHashHistory(),
  routes
});

export default myRouter;

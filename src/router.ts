import {
  createRouter,
  createWebHashHistory,
  NavigationFailure
} from "vue-router";

const routes = [
  { path: "/", component: () => import("./components/HomePage.vue") },
  { path: "/input", component: () => import("./components/TextInput.vue") },
  { path: "/hello", component: () => import("./components/HelloWorld.vue") },
  { path: "/parser", component: () => import("./components/ShowParser.vue") },
  { path: "/tree", component: () => import("./components/EchartsTree.vue") },
  { path: "/demo", component: () => import("./_demo/Demo.vue") },
  { path: "/show", component: () => import("./components/ShowAll.vue") },

  // Final
  {
    path: "/:pathMatch(.*)*",
    component: () => import("./components/FourZeroFour.vue")
  }
];

const myRouter = createRouter({
  history: createWebHashHistory(),
  routes
});

export default myRouter;

export function showAll(): Promise<void | NavigationFailure | undefined> {
  return myRouter.push("show");
}

export function input(): Promise<void | NavigationFailure | undefined> {
  return myRouter.push("input");
}

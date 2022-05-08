import { createStore } from "vuex";

const myStore = createStore({
  state() {
    return {
      userId: 0
    };
  },
  mutations: {
    setUserId(state, userId) {
      state.userId = userId;
    }
  }
});

export default myStore;

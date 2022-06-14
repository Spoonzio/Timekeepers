// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
require("dotenv").config();
import Vue from "vue";
import App from "./App";
import ElementUI from "element-ui";
import "element-ui/lib/theme-chalk/index.css";
import router from "./router";
import axios from "axios";
import VueCookie from "vue-cookie";
import constants from "./commom/constants";
import locale from "element-ui/lib/locale/lang/en";

Vue.config.productionTip = false;

Vue.prototype.$axios = axios;
Vue.prototype.$constants = constants;
Vue.use(VueCookie);
Vue.use(ElementUI, { locale });

/* eslint-disable no-new */
new Vue({
  el: "#app",
  router,
  components: { App },
  template: "<App/>"
});

router.beforeEach((to, from, next) => {
  // chrome
  document.body.scrollTop = 0;
  // firefox
  document.documentElement.scrollTop = 0;
  // safari
  window.pageYOffset = 0;
  next();
});

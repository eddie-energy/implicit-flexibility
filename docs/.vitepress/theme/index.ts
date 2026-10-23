import type { Theme } from "vitepress";
import DefaultTheme from "vitepress/theme";
import Img from "./Img.vue";

export default {
  extends: DefaultTheme,

  enhanceApp({ app }) {
    app.component("Img", Img);
  },
} satisfies Theme;
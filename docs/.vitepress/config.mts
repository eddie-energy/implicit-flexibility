import { defineConfig } from "vitepress";
import renderMarkdownImage from "./theme/renderMarkdownImage";

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "EDDIE Implicit Flexibility Framework",
  description: "Documentation for the EDDIE Implicit Flexibility Framework (Tariff Information)",
  head: [["link", { rel: "icon", href: "/images/favicon.svg" }]],
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: "Home", link: "/" },
    ],

    sidebar: [
      {
        text: "Guide",
        link: "/guide/",
        items: [
          { text: "Overview", link: "/guide/overview" },
          { text: "Architecture", link: "/guide/architecture" },
          { text: "API", link: "/guide/api" },
          {
            text: "Data Sources",
            items: [
              { text: "E-Control (Austria)", link: "/guide/datasources/datasource-at-econtrol" },
            ],
          },
        ],
      },
      {
        text: "Extending",
        link: "/extend/",
        items: [
          { text: "Add Data Source", link: "/extend/add-datasource" },
          { text: "Implement Tariff Discovery", link: "/extend/implement-workflow" },
        ],
      },
    ],

    socialLinks: [
      { icon: "github", link: "https://github.com/eddie-energy" },
    ],
  },

  vue: {
    template: {
      transformAssetUrls: {
        Img: ["src"],
      },
    },
  },

  markdown: {
    config: (md) => {
      md.renderer.rules.image = renderMarkdownImage;
    },
  },
});

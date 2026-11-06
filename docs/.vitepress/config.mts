import { defineConfig } from "vitepress";
import renderMarkdownImage from "./theme/renderMarkdownImage";

// https://vitepress.dev/reference/site-config
export default defineConfig({
  title: "EDDIE Implicit Flexibility",
  description: "Documentation for the EDDIE Implicit Flexibility Framework (Tariff Information)",
  head: [["link", { rel: "icon", href: "/images/favicon.svg" }]],
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: "Home", link: "/" },
    ],

    sidebar: [
      {
        text: "Running",
        link: "/run/",
        items: [
          { text: "Overview", link: "/run/overview" },
          { text: "Architecture", link: "/run/architecture" },
          {
            text: "Data Sources",
            items: [
              { text: "E-Control (Austria)", link: "/run/datasources/datasource-at-econtrol" },
              { text: "ESIOS (Spain)", link: "/run/datasources/datasource-es-esios" },
            ],
          },
        ],
      },
      {
        text: "Integrating",
        link: "/integrate/",
        items: [
          { text: "API", link: "/integrate/api" },
        ],
      },
      {
        text: "Extending",
        link: "/extend/",
        items: [
          { text: "Technical Architecture", link: "/extend/technical-architecture" },
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

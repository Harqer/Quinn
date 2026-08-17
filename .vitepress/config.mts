import { defineConfig } from 'vitepress'

export default defineConfig({
  title: "Deep Wiki",
  description: "AI-generated documentation site",
  themeConfig: {
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Guide', link: '/docs/guide' }
    ],
    sidebar: [
      {
        text: 'Introduction',
        items: [
          { text: 'Guide', link: '/docs/guide' },
        ]
      }
    ]
  }
})

import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'HiveServer2 JDBC Driver',
  description: 'Third-party builds of the HiveServer2 JDBC Driver',
  base: '/hive-server2-jdbc-driver/',
  
  themeConfig: {
    logo: '/logo.svg',
    
    nav: [
      { text: 'Home', link: '/' },
      { text: 'Quick Start', link: '/quick-start' },
      { text: 'Documentation', 
        items: [
          { text: 'Background', link: '/background' },
          { text: 'FAQ', link: '/faq' },
          { text: 'Contributing', link: '/contributing' }
        ]
      },
      { text: 'Changelog', link: '/changelog' },
      { text: 'GitHub', link: 'https://github.com/linghengqian/hive-server2-jdbc-driver' }
    ],

    sidebar: [
      {
        text: 'Getting Started',
        items: [
          { text: 'Introduction', link: '/' },
          { text: 'Quick Start', link: '/quick-start' },
          { text: 'Background', link: '/background' }
        ]
      },
      {
        text: 'Documentation',
        items: [
          { text: 'FAQ', link: '/faq' },
          { text: 'Contributing', link: '/contributing' }
        ]
      },
      {
        text: 'Release Information',
        items: [
          { text: 'Changelog', link: '/changelog' }
        ]
      }
    ],

    socialLinks: [
      { icon: 'github', link: 'https://github.com/linghengqian/hive-server2-jdbc-driver' }
    ],

    footer: {
      message: 'Released under the Apache License 2.0.',
      copyright: 'Copyright © 2025 Qiheng He'
    },

    search: {
      provider: 'local'
    },

    editLink: {
      pattern: 'https://github.com/linghengqian/hive-server2-jdbc-driver/edit/master/docs/:path',
      text: 'Edit this page on GitHub'
    },

    lastUpdated: {
      text: 'Updated at',
      formatOptions: {
        dateStyle: 'full',
        timeStyle: 'medium'
      }
    }
  },

  markdown: {
    theme: 'github-dark',
    lineNumbers: true
  },

  head: [
    ['link', { rel: 'icon', href: '/favicon.ico' }],
    ['meta', { name: 'theme-color', content: '#3c8772' }]
  ]
})
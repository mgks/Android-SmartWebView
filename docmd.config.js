// docmd.config.js: basic config for docmd
module.exports = {
  // Core Site Metadata
  siteTitle: 'Android Smart WebView',
  // Define a base URL for your site, crucial for SEO and absolute paths
  // No trailing slash
  siteUrl: '/documentation', // Replace with your actual deployed URL

  // Logo Configuration
  logo: {
    light: '/assets/images/swv-logo.png', // Path relative to outputDir root
    dark: '/assets/images/swv-logo.png',   // Path relative to outputDir root
    alt: 'swv logo',                      // Alt text for the logo
    href: '/',                              // Link for the logo, defaults to site root
  },

  // Directory Configuration
  srcDir: 'docmd',       // Source directory for Markdown files
  outputDir: 'documentation',    // Directory for generated static site

  // Search Configuration
  search: true,        // Enable/disable search functionality

  // Build Options
  minify: true,        // Enable/disable HTML/CSS/JS minification

  // Sidebar Configuration
  sidebar: {
    collapsible: true,        // or false to disable
    defaultCollapsed: false,  // or true to start collapsed
  },

  // Theme Configuration
  theme: {
    name: 'sky',            // Themes: 'default', 'sky'
    defaultMode: 'light',   // Initial color mode: 'light' or 'dark'
    enableModeToggle: true, // Show UI button to toggle light/dark modes
    positionMode: 'top', // 'top' or 'bottom' for the theme toggle
    codeHighlight: true,    // Enable/disable codeblock highlighting and import of highlight.js
    customCss: [            // Array of paths to custom CSS files
      // '/assets/css/custom.css', // Custom TOC styles
    ]
  },

  // Custom JavaScript Files
  customJs: [  // Array of paths to custom JS files, loaded at end of body
    // '/assets/js/custom-script.js', // Paths relative to outputDir root
    '/assets/js/docmd-image-lightbox.js', // Image lightbox functionality
  ],

  // Content Processing
  autoTitleFromH1: true, // Set to true to automatically use the first H1 as page title
  copyCode: true, // Enable/disable the copy code button on code blocks

  // Plugins Configuration
  // Plugins are configured here. docmd will look for these keys.
  plugins: {
    // SEO Plugin Configuration
    // Most SEO data is pulled from page frontmatter (title, description, image, etc.)
    // These are fallbacks or site-wide settings.
    seo: {
      // Default meta description if a page doesn't have one in its frontmatter
      defaultDescription: 'Smart WebView is a versatile and lightweight project designed to help you quickly convert your website or web application into a native mobile app.',
      openGraph: { // For Facebook, LinkedIn, etc.
        // siteName: 'docmd Documentation', // Optional, defaults to config.siteTitle
        // Default image for og:image if not specified in page frontmatter
        // Path relative to outputDir root
        defaultImage: '/assets/images/cover-swv.png',
      },
      twitter: { // For Twitter Cards
        cardType: 'summary_large_image',     // 'summary', 'summary_large_image'
        // siteUsername: '@docmd_handle',    // Your site's Twitter handle (optional)
        // creatorUsername: '@your_handle',  // Default author handle (optional, can be overridden in frontmatter)
      }
    },
    // Analytics Plugin Configuration
    analytics: {
      // Google Analytics 4 (GA4)
      googleV4: {
        measurementId: 'G-8QVBDQ4KM1' // Replace with your actual GA4 Measurement ID
      }
    },
    // Enable Sitemap plugin
    sitemap: {
      defaultChangefreq: 'weekly',
      defaultPriority: 0.8
    }
    // Add other future plugin configurations here by their key
  },

  // "Edit this page" Link Configuration
  editLink: {
    enabled: true,
    // The URL to the folder containing your docs in the git repo
    // Note: It usually ends with /edit/main/docs or /blob/main/docs
    baseUrl: 'https://github.com/mgks/Android-SmartWebView/edit/main/docs',
    text: 'Edit this page on GitHub'
  },

  // Navigation Structure (Sidebar)
  // Icons are kebab-case names from Lucide Icons (https://lucide.dev/)
  navigation: [
      { title: 'Welcome', path: '/', icon: 'home' }, // Corresponds to docs/index.md
      { title: 'Getting Started', path: '/getting-started', icon: 'rocket'},
      { title: 'Configuration', path: '/configuration', icon: 'settings'},
      { title: 'Customization', path: '/customization', icon: 'settings-2'},
      { title: 'Features',
        path: '/features',
        icon: 'zap',
        collapsible: false,
        children: [
          { title: 'File Handling', path: '/features', icon: 'file'},
          { title: 'Firebase Messaging', path: '/features/firebase-messaging', icon: 'bell'},
          { title: 'Analytics', path: '/features/analytics', icon: 'chart-line'},
          { title: 'Navigation', path: '/features/navigation', icon: 'arrow-right-from-line'},
          { title: 'Sharing', path: '/features/sharing', icon: 'share-2'},
          { title: 'Printing', path: '/features/printing', icon: 'printer'},
          { title: 'Dark Mode & Theming', path: '/features/dark-mode', icon: 'moon'},
        ]
      },
      { title: 'Plugins',
        path: '/plugins',
        icon: 'plug',
        collapsible: true,
        children: [
          { title: 'Architecture', path: '/plugins', icon: 'file-code'},
          { title: 'Creating Plugins', path: '/plugins/creating-plugins', icon: 'file-code'},
          { title: 'Playground', path: '/plugins/playground', icon: 'file-code'},
          { title: 'Toast', path: '/plugins/toast', icon: 'file-code'},
          { title: 'Location Access', path: '/plugins/location', icon: 'file-code'},
          { title: 'Rating System', path: '/plugins/rating-system', icon: 'file-code'},
          { title: 'Dialogs & Alerts', path: '/plugins/dialog', icon: 'file-code'},
          { title: 'Admob', path: '/plugins/admob', icon: 'file-code'},
          { title: 'QR & Barcode Reader', path: '/plugins/qr-barcode-reader', icon: 'file-code'},
          { title: 'Biometric Auth', path: '/plugins/biometric-auth', icon: 'file-code'},
          { title: 'Image Compression', path: '/plugins/image-compression', icon: 'file-code'},
        ]
      },
      { title: 'Play Store Guide', path: '/play-store-guide', icon: 'shield-check'},
      { title: 'Contributing', path: '/contributing', icon: 'users'},
      { title: 'License', path: '/license', icon: 'file-code'},
      // External links:
      { title: 'GitHub', path: 'https://github.com/mgks/Android-SmartWebView', icon: 'github', external: true },
      { title: 'Support the Project', path: 'https://github.com/sponsors/mgks', icon: 'heart', external: true },
    ],
    
  pageNavigation: true, // Enable previous / next page navigation at the bottom of each page

  // Sponsor Ribbon Configuration
  Sponsor: {
    enabled: false,
    title: 'Sponsor',
    link: 'https://github.com/sponsors/mgks',
  },

  // Footer Configuration
  // Markdown is supported here.
  footer: '© ' + new Date().getFullYear() + ' Project.',

  // Favicon Configuration
  // Path relative to outputDir root
  favicon: '/assets/favicon.ico',
};

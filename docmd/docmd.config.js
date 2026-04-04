// docmd.config.js: configuración básica para docmd
module.exports = {
  // Metadatos principales del sitio
  siteTitle: 'ProfileBio Docs',        // Cambia según tu proyecto
  siteUrl: 'https://www.profilebio.me', // Tu dominio (sin barra final)

  // Configuración del logo (debes agregar tus propias imágenes en /assets/images/)
  logo: {
    light: '/assets/images/logo-light.png', // Ruta relativa a la carpeta de salida (site/)
    dark: '/assets/images/logo-dark.png',
    alt: 'ProfileBio Logo',
    href: 'https://www.profilebio.me',
  },

  // Directorios de trabajo
  srcDir: 'content',   // Carpeta con los archivos Markdown
  outputDir: 'site',   // Carpeta donde se generará el sitio estático

  // Búsqueda
  search: true,

  // Minificación
  minify: true,

  // Barra lateral (sidebar)
  sidebar: {
    collapsible: true,
    defaultCollapsed: false,
  },

  // Tema y apariencia
  theme: {
    name: 'sky',               // Temas: 'default', 'sky'
    defaultMode: 'light',
    enableModeToggle: true,
    positionMode: 'top',
    codeHighlight: true,
    customCss: [],             // Agrega aquí tus CSS personalizados
  },

  // JavaScript personalizado
  customJs: [],

  // Procesamiento de contenido
  autoTitleFromH1: true,
  copyCode: true,

  // Plugins (SEO, Analytics, Sitemap)
  plugins: {
    seo: {
      defaultDescription: 'Documentación y guías para ProfileBio.',
      openGraph: {
        defaultImage: '/assets/images/cover.png', // Imagen por defecto para redes sociales
      },
      twitter: {
        cardType: 'summary_large_image',
      },
    },
    analytics: {
      googleV4: {
        measurementId: 'G-XXXXXXXXXX', // Reemplaza con tu ID de medición de GA4
      },
    },
    sitemap: {
      defaultChangefreq: 'weekly',
      defaultPriority: 0.8,
    },
  },

  // Enlace "Editar esta página" (deshabilitado hasta que tengas un repositorio)
  editLink: {
    enabled: false,              // Cambia a true y define baseUrl cuando tengas un repo
    baseUrl: '',                // Ejemplo: 'https://github.com/tuusuario/tu-repo/edit/main/docs'
    text: 'Editar esta página en GitHub',
  },

  // Navegación principal (barra lateral)
  // Ajusta las rutas según los archivos .md que tengas en la carpeta 'content'
  navigation: [
    { title: 'Inicio', path: '/', icon: 'home' },
    { title: 'Empezando', path: '/getting-started', icon: 'rocket' },
    { title: 'Configuración', path: '/configuration', icon: 'settings' },
    { title: 'Características', path: '/features', icon: 'zap' },
    // Ejemplo de submenú:
    // {
    //   title: 'Guías',
    //   path: '/guides',
    //   icon: 'book-open',
    //   collapsible: true,
    //   children: [
    //     { title: 'Guía 1', path: '/guides/guide1', icon: 'file-text' },
    //     { title: 'Guía 2', path: '/guides/guide2', icon: 'file-text' },
    //   ],
    // },
    // Enlaces externos
    { title: 'GitHub', path: 'https://github.com/tuusuario/tu-repo', icon: 'github', external: true },
    { title: 'Soporta el proyecto', path: 'https://github.com/sponsors/tuusuario', icon: 'heart', external: true },
  ],

  // Navegación entre páginas (anterior/siguiente)
  pageNavigation: true,

  // Cinta de patrocinio (opcional)
  Sponsor: {
    enabled: false,  // Actívalo si tienes enlace de patrocinio
    title: 'Sponsor',
    link: 'https://github.com/sponsors/tuusuario',
  },

  // Pie de página (soporta Markdown)
  footer: '© ' + new Date().getFullYear() + ' ProfileBio. Todos los derechos reservados.',

  // Favicon
  favicon: '/assets/favicon.ico',
};

# Documentation

This directory contains the VitePress-powered documentation for the HiveServer2 JDBC Driver project.

## Development

To run the documentation locally:

```bash
npm install
npm run docs:dev
```

This will start a local development server at `http://localhost:5173/hive-server2-jdbc-driver/`

## Building

To build the documentation for production:

```bash
npm run docs:build
```

The built files will be in `docs/.vitepress/dist/`

## Preview

To preview the built documentation:

```bash
npm run docs:preview
```

## Structure

- `index.md` - Home page
- `quick-start.md` - Quick start guide
- `background.md` - Project background and motivation
- `faq.md` - Frequently asked questions
- `contributing.md` - Contributing guidelines
- `changelog.md` - Release notes and changelog
- `.vitepress/config.mjs` - VitePress configuration

## Deployment

The documentation is automatically deployed to GitHub Pages when changes are pushed to the master branch via the GitHub Actions workflow in `.github/workflows/deploy-docs.yml`.
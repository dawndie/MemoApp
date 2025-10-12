# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the **Next.js frontend** for MemoApp - a note-taking application. It is intended to replace the existing Angular frontend (`MemoApp_Frontend/`) and communicates with a Spring Boot backend API.

**Key Context:**
- Part of a larger MemoApp monorepo at `/Users/voluongbang/Programing/Side Project/Java_Learning/MemoApp/`
- Backend API runs on `http://localhost:1919/api`
- Previous Angular frontend ran on port 6565
- Backend repository: `MemoApp_Backend/`
- Project documentation: `docs/` directory in parent MemoApp folder

## Technology Stack

- **Framework**: Next.js 15.5.4 with App Router
- **React**: 19.1.0 (with React DOM)
- **Language**: TypeScript 5.x
- **Styling**: Tailwind CSS v4 with PostCSS
- **Build Tool**: Turbopack (Next.js)
- **Linting**: ESLint 9.x with Next.js config
- **Fonts**: Geist Sans and Geist Mono (Google Fonts)

## Common Commands

### Development
```bash
# Start development server with Turbopack
npm run dev

# The app runs on http://localhost:3000 by default
```

### Building
```bash
# Create production build with Turbopack
npm run build

# Start production server (after build)
npm start
```

### Code Quality
```bash
# Run ESLint
npm run lint

# Note: No test scripts configured yet
```

## Architecture

### Directory Structure

```
memoapp-frontend-next/
├── src/
│   └── app/              # Next.js App Router
│       ├── layout.tsx    # Root layout with font configuration
│       ├── page.tsx      # Home page
│       └── globals.css   # Global styles with Tailwind and theme
├── public/               # Static assets (SVG icons)
├── next.config.ts        # Next.js configuration
├── tsconfig.json         # TypeScript config with path aliases (@/*)
├── postcss.config.mjs    # PostCSS with Tailwind plugin
└── eslint.config.mjs     # ESLint configuration
```

### Key Files

**`src/app/layout.tsx`**
- Root layout component
- Configures Geist Sans and Geist Mono fonts
- Sets up base HTML structure with font variables
- Defines metadata (title, description)

**`src/app/page.tsx`**
- Home page component (currently default Next.js template)
- Uses Tailwind CSS for styling
- Responsive grid layout

**`src/app/globals.css`**
- Imports Tailwind CSS
- Defines CSS custom properties for theming:
  - `--background` and `--foreground` colors
  - Light/dark mode support via `prefers-color-scheme`
- Font family configuration via `@theme inline`

**`tsconfig.json`**
- Path alias: `@/*` maps to `./src/*`
- Target: ES2017
- Strict mode enabled
- Bundler module resolution

### Next.js App Router

This project uses the **App Router** (not Pages Router):
- All routes are in `src/app/`
- `layout.tsx` for shared layouts
- `page.tsx` for route pages
- Server Components by default
- Client Components require `'use client'` directive

## Backend Integration

### API Endpoints

The backend API (`http://localhost:1919/api`) provides:

- `GET /api/memos` - Get all memos
- `GET /api/memos/{id}` - Get single memo
- `POST /api/memos` - Create new memo
- `PUT /api/memos/{id}` - Update memo
- `DELETE /api/memos/{id}` - Delete memo

### Memo Model

```typescript
interface Memo {
  id?: number;
  title: string;
  content: string;
  priority?: string;  // Backend supports priority field
  createdAt?: string;
  updatedAt?: string;
}
```

### Environment Configuration

Backend API URL should be configured in environment variables:
- Development: `NEXT_PUBLIC_API_URL=http://localhost:1919/api`
- Production: Set appropriately for deployment

**Note**: Next.js environment variables starting with `NEXT_PUBLIC_` are exposed to the browser.

## Styling with Tailwind CSS v4

This project uses **Tailwind CSS v4** (new architecture):

- Import via `@import "tailwindcss"` in CSS
- Custom theme defined with `@theme inline` directive
- CSS custom properties for theming (`--color-background`, `--color-foreground`, `--font-sans`, `--font-mono`)
- No `tailwind.config.js` needed (v4 uses CSS-based configuration)
- PostCSS plugin: `@tailwindcss/postcss`

### Theme System

```css
:root {
  --background: #ffffff;  /* Light mode */
  --foreground: #171717;
}

@media (prefers-color-scheme: dark) {
  :root {
    --background: #0a0a0a;  /* Dark mode */
    --foreground: #ededed;
  }
}
```

## Development Guidelines

### Component Structure

When creating new components:
1. Use TypeScript with proper type definitions
2. Prefer Server Components unless client interactivity is needed
3. Use `'use client'` directive only when necessary (state, effects, event handlers)
4. Follow Next.js naming conventions (`page.tsx`, `layout.tsx`, `loading.tsx`, `error.tsx`)

### State Management

For this application size, consider:
- React hooks (`useState`, `useEffect`) for local state
- Context API for shared state across components
- Server Components for data fetching when possible
- Consider SWR or React Query for API data fetching and caching

### API Calls

- Use `fetch` API (built into Next.js)
- Server Components can fetch data directly
- Client Components should use `useEffect` or data fetching libraries
- Handle loading and error states appropriately
- Consider API route handlers (`app/api/`) as a proxy layer if needed

### Turbopack

This project uses Turbopack (Next.js bundler):
- Faster than Webpack for development
- Enabled via `--turbopack` flag in `npm run dev` and `npm run build`
- Hot Module Replacement (HMR) is faster
- Build times are significantly improved

## Migration from Angular Frontend

When migrating features from `MemoApp_Frontend/`:

### Component Mapping

**Angular → Next.js**
- `MemoList` component → Create `app/memos/page.tsx` or `app/page.tsx`
- `MemoForm` component → Create `app/memos/new/page.tsx` and `app/memos/[id]/edit/page.tsx`
- `MemoService` → Create API utility functions or use Server Actions

### Routing

**Angular Routes → Next.js Routes**
- `/` → `app/page.tsx`
- `/memo/new` → `app/memos/new/page.tsx`
- `/memo/edit/:id` → `app/memos/[id]/edit/page.tsx`

### Forms

- Replace Angular Reactive Forms with React Hook Form or native form handling
- Consider using Server Actions for form submissions (Next.js 14+)
- Implement client-side validation with libraries like Zod

### State vs Observables

- Replace RxJS Observables with Promises or async/await
- Use React hooks instead of Angular lifecycle methods
- Consider SWR/React Query for data synchronization

## Git Workflow

- Main branch: Not specified (check with team)
- Current branch: `feat-12`
- Backend endpoint recently updated (see commit `26c28abe`)
- Recent features include confirmation modal, Swagger support

## Related Documentation

For complete system understanding, refer to parent MemoApp documentation:
- `../docs/README.md` - System overview
- `../docs/frontend-guide.md` - Original Angular frontend guide
- `../docs/api-documentation.md` - Backend API reference
- `../docs/system-architecture.md` - Overall architecture
- `../docs/database-schema.md` - Database structure

## Common Tasks

### Add a New Page

```bash
# Create directory and page file
mkdir -p src/app/new-route
touch src/app/new-route/page.tsx
```

### Create an API Route

```bash
# Create API route handler
mkdir -p src/app/api/route-name
touch src/app/api/route-name/route.ts
```

### Add Environment Variables

1. Create `.env.local` file (not committed)
2. Add variables with `NEXT_PUBLIC_` prefix for client-side access
3. Reference in code: `process.env.NEXT_PUBLIC_API_URL`

### Debugging

- Development errors show in browser overlay
- Check terminal for server-side errors
- Use React DevTools browser extension
- Enable verbose logging: `DEBUG=* npm run dev`

## Current Status

This is a **new Next.js frontend** bootstrapped from `create-next-app`. The application currently shows the default Next.js template and needs:

1. API integration with backend at `localhost:1919`
2. Memo CRUD functionality implementation
3. Form components for creating/editing memos
4. List view for displaying memos
5. Error handling and loading states
6. Priority support (backend has this field)
7. Testing setup (Jest, React Testing Library, or Playwright)

The previous Angular frontend (`MemoApp_Frontend/`) can serve as a reference for feature requirements and user flows.

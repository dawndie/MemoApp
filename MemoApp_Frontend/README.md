# MemoApp Frontend

Modern Angular frontend application for the MemoApp project with clean, responsive design.

## Features

- **Clean Modern Design**: Simple, intuitive interface with custom CSS styling
- **Priority Management**: Set and update memo priorities (High, Medium, Low) with color-coded indicators
- **Priority Statistics**: Real-time overview showing priority distribution with percentages
- **Bulk Operations**: Select multiple memos and update priorities in bulk
- **Responsive Card Layout**: Clean memo cards with hover effects and professional styling
- **Advanced Filtering**: Filter memos by priority level
- **Flexible Sorting**: Sort by priority or creation date (ascending/descending)
- **Form Controls**: Native HTML form elements with custom styling
- **Form Validation**: Client-side validation with error handling
- **Loading States**: Clean loading indicators and empty states
- **Mobile Responsive**: Fully responsive design optimized for all device sizes

## Technology Stack

- **Angular 20+**: Latest Angular framework with standalone components
- **TypeScript**: Type-safe development
- **RxJS**: Reactive programming for API calls
- **CSS3**: Modern styling with flexbox and grid
- **Native HTML5**: Standard form controls and semantic markup

## Prerequisites

- Node.js (v18 or higher)
- npm
- Running MemoApp backend on http://localhost:1919

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```
   *Note: The project includes an `.npmrc` file with `legacy-peer-deps=true` to resolve Angular version conflicts. You may see deprecation warnings from transitive dependencies - these are from Angular/npm ecosystem packages and are safe to ignore.*

2. Start the development server:
   ```bash
   npm start
   ```

3. Open your browser to http://localhost:6565

## Available Scripts

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm test` - Run unit tests
- `npm run lint` - Run ESLint

## Project Structure

```
src/
├── app/
│   ├── components/
│   │   ├── memo-list/           # Main memo display with responsive cards
│   │   ├── memo-form/           # Create/edit memo form
│   │   ├── priority-selector/   # Priority selection dropdown component
│   │   └── priority-stats/      # Priority statistics dashboard
│   ├── models/
│   │   └── memo.model.ts        # Memo and Priority interface definitions
│   ├── services/
│   │   └── memo.service.ts      # API service for backend communication
│   ├── app.config.ts            # Angular app configuration
│   └── environments/            # Environment configurations
├── styles.css                   # Global styles with custom CSS framework
└── index.html                  # Main HTML file
```

## API Integration

The frontend communicates with the Spring Boot backend API:

### Memo Operations
- **GET** `/api/memos` - Fetch all memos (supports priority and sort query params)
- **GET** `/api/memos/{id}` - Fetch memo by ID
- **POST** `/api/memos` - Create new memo
- **PUT** `/api/memos/{id}` - Update existing memo
- **DELETE** `/api/memos/{id}` - Delete memo

### Priority Management
- **PATCH** `/api/memos/{id}/priority` - Update memo priority
- **PATCH** `/api/memos/bulk/priority` - Bulk update memo priorities
- **GET** `/api/memos/stats/priority` - Get priority statistics

### Query Parameters
- `priority`: Filter by priority level (high, medium, low)
- `sort`: Sort memos (priority_desc, priority_asc, created_desc, created_asc)

## Styling Architecture

This application uses a clean, modern CSS architecture:

### Design System
- **Custom CSS**: Hand-crafted styles for optimal performance
- **CSS Grid & Flexbox**: Modern layout techniques for responsive design
- **Color Palette**: Consistent color scheme across components
- **Typography**: Clean, readable font hierarchy
- **Component Styling**: Modular CSS with component-specific stylesheets

### Key Design Elements
- **Card-based Layout**: Clean memo cards with subtle shadows and hover effects
- **Priority Indicators**: Color-coded badges for visual priority identification
- **Responsive Grid**: Adaptive layout that works on all screen sizes
- **Interactive Elements**: Smooth transitions and hover states
- **Form Controls**: Native HTML elements with custom styling

### Performance Benefits
- **Smaller Bundle Size**: No external UI library dependencies
- **Faster Load Times**: Optimized CSS without unused components
- **Better Control**: Full customization of all visual elements

## Environment Configuration

Update `src/environments/environment.ts` to change the API URL:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:1919/api'
};
```

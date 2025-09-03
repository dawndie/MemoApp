# MemoApp Frontend

Angular frontend application for the MemoApp project.

## Features

- View all memos in a responsive card layout
- Create new memos with title and content
- Edit existing memos
- Delete memos with confirmation
- Responsive design for mobile and desktop
- Form validation
- Error handling and loading states

## Prerequisites

- Node.js (v18 or higher)
- npm
- Running MemoApp backend on http://localhost:1919

## Getting Started

1. Install dependencies:
   ```bash
   npm install
   ```

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
│   │   ├── memo-list/       # Display list of memos
│   │   └── memo-form/       # Create/edit memo form
│   ├── models/
│   │   └── memo.model.ts    # Memo interface definitions
│   ├── services/
│   │   └── memo.service.ts  # API service for backend communication
│   └── environments/        # Environment configurations
├── styles.css               # Global styles
└── index.html              # Main HTML file
```

## API Integration

The frontend communicates with the Spring Boot backend API:

- **GET** `/api/memos` - Fetch all memos
- **GET** `/api/memos/{id}` - Fetch memo by ID
- **POST** `/api/memos` - Create new memo
- **PUT** `/api/memos/{id}` - Update existing memo
- **DELETE** `/api/memos/{id}` - Delete memo

## Environment Configuration

Update `src/environments/environment.ts` to change the API URL:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:1919/api'
};
```

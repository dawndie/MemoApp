# Frontend Issue Development Command

## UsageW
```
/frontend-issue <issue-number>
```

## Test Delegation Flow

### Phase 1: Requirements Analysis
```
frontend-specialist: "Analyze GitHub issue #$ARGUMENTS and create detailed frontend implementation plan for Angular application. Return component architecture design, file changes needed, UI/UX specifications, service integration requirements, and testing strategy."
```
Use the **frontend-specialist** agent to:
- Fetch and analyze GitHub issue requirements using `gh` command
- Catalog software requirements 
- Architect and design technical specifications
- Research Angular best practices 
- Document an effective implementation strategy

### Phase 2: Implementation Orchestration
- Create feature branch (format: `feat-<issue-number>`)
- Implement changes following analysis plan:
  - Component creation and updates
  - Service layer for API integration
  - TypeScript interfaces and models
  - Template and styling implementation
  - Routing configuration
  - State management updates

### Phase 3: Quality Assurance
- Run `npm run test` to validate all tests pass
- Run `npm run build` to ensure compilation success
- Run `npm run lint` for code quality validation
- **Playwright End-to-End Testing with Visual Evidence**:
  - Start the application (`npm start` or `npm run dev`)
  - Use Playwright MCP to test implemented UI features
  - Capture screenshots of key functionality states
  - Test user interactions, form submissions, data display
  - Validate responsive design across different sizes
  - Document test scenarios and results with visual proof

  ### Phase 4: Documentation & Git Management
- Create task specification and workflow action log in `tasks_yyyymmdd_issue-x_[task-content].md`
- Use **git-manager** agent to:
  - Stage all changes
  - Create conventional commit messages base on `tasks_yyyymmdd_issue-x_[task-content].md` file
  - Push branch to remote
  - Create comprehensive pull request
  - Comment on PR with test results and implementation details

## Task Logging Requirements
Each backend-issue execution must create a task log file:

**Format**: `tasks_yyyymmdd_issue-3_implement-priority-functionality.md`


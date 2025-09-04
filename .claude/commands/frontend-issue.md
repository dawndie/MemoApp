# Frontend Issue Development Command

## Usage
```
/frontend-issue <issue-number>
```

## Context
- Orchestrated frontend development workflow with specialized sub-agents
- Focuses on Angular, TypeScript, and modern frontend development with enterprise quality
- Uses general-purpose agent for analysis, then delegates implementation to appropriate agents

## Your Role
You are a frontend development orchestrator that coordinates multiple specialized agents to deliver production-ready frontend features.

## Workflow Process

### Phase 1: Requirements Analysis
Use the **frontend-specialist** agent to:
- Fetch and analyze GitHub issue requirements using `gh` command
- Assess UI/UX complexity and technical scope
- Design component architecture and implementation plan
- Research Angular best practices and patterns
- Return detailed implementation roadmap with specific file changes needed
- Create task specification and workflow action log in `tasks_yyyymmdd_frontend-issue-x_[task-content].md`

### Phase 2: Implementation Orchestration
- Create feature branch (format: `feat-frontend-<issue-number>`)
- Implement changes following analysis plan:
  - Component creation and updates
  - Service layer for API integration
  - TypeScript interfaces and models
  - Template and styling implementation
  - Routing configuration
  - State management updates
- Write comprehensive tests (unit + integration)

### Phase 3: Quality Assurance
- Run `npm run test` to validate all tests pass
- Run `npm run build` to ensure compilation success
- Run `npm run lint` for code quality validation
- Validate against Angular and TypeScript best practices
- Test UI functionality and responsiveness

### Phase 4: Documentation & Git Management
- Update specification and workflow action log in `tasks_yyyymmdd_frontend-issue-x_[task-content].md`
- Use **git-manager** agent to:
  - Stage all changes
  - Create conventional commit messages based on `tasks_yyyymmdd_frontend-issue-x_[task-content].md` file
  - Push branch to remote
  - Create comprehensive pull request
  - Comment on PR with test results and implementation details

## Task Logging Requirements
Each frontend-issue execution must create a task log file:

**Format**: `tasks_yyyymmdd_frontend-issue-3_implement-memo-priority-ui.md`

**Content Structure**:
```markdown
# Task: [Description] - Frontend Issue #[number]
**Date**: yyyy-mm-dd
**Issue**: #[number]
**Branch**: feat-frontend-[number]
**Status**: Completed

## Requirements Analysis
[Analysis results from Phase 1]

## Implementation Summary
[What was implemented]

## Components Modified/Created
[List of components with descriptions]

## Services Modified/Created
[List of services with descriptions]

## UI/UX Features Added
[New frontend features and interactions]

## Test Results
[Test execution results]

## Build & Quality Metrics
[Build status, lint results, test coverage, etc.]
```

## Task Delegation Flow

1. **Analysis Phase**:
```
planner-researcher: "Analyze GitHub issue #$ARGUMENTS and create detailed frontend implementation plan for Angular application. Return component architecture design, file changes needed, UI/UX specifications, service integration requirements, and testing strategy."
```

2. **Implementation Phase**: Parent orchestrates based on analysis
3. **Quality Phase**: Validate and finalize

## Expected Flow
1. planner-researcher → Returns implementation plan
2. Parent receives plan → Implements following the design
3. Call appropriate agents for specific tasks (testing, git, etc.)
4. Deliver production-ready frontend feature

## Quality Standards
- Modern Angular practices and patterns
- TypeScript best practices
- Responsive design and accessibility
- Component-based architecture
- Comprehensive testing (85%+ coverage)
- Performance optimization
- Proper documentation and git workflow
- Cross-browser compatibility
# Backend Issue Development Command

## Usage
```
/backend-issue <issue-number>
```

## Context
- Orchestrated backend development workflow with specialized sub-agents
- Focuses on Spring Boot, REST API, and database development with enterprise quality
- Uses backend-specialist for analysis, then delegates implementation to appropriate agents

## Your Role
You are a backend development orchestrator that coordinates multiple specialized agents to deliver production-ready backend features.

## Workflow Process

### Phase 1: Requirements Analysis
```
backend-specialist: "Analyze GitHub issue #$ARGUMENTS and create detailed backend implementation plan for Spring boot application. Return cesign enterprise-grade architecture and implementation plan, file changes and validate against enterprise standards and best practices"
```
Use the **backend-issue-analyzer** agent to:
- Fetch and analyze GitHub issue requirements using `gh` command
- Assess technical complexity and scope
- Design enterprise-grade architecture and implementation plan
- Return detailed implementation roadmap with specific file changes needed

### Phase 2: Implementation Orchestration
- Create feature branch (format: `feat-<issue-number>`)
- Implement changes following analysis plan:
  - Entity/Model updates
  - Repository enhancements
  - Service layer business logic
  - Controller REST endpoints
  - DTOs for request/response handling
- Write comprehensive tests (unit + integration)

### Phase 3: Quality Assurance
- Run `./gradlew test` to validate all tests pass
- Run `./gradlew build` to ensure compilation success
- Validate against enterprise standards and best practices

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

**Content Structure**:
```markdown
# Task: [Description] - Issue #[number]
**Date**: yyyy-mm-dd
**Issue**: #[number]
**Branch**: feat-[number]
**Status**: Completed

## Requirements Analysis
[Analysis results from Phase 1]

## Implementation Summary
[What was implemented]

## Files Modified/Created
[List of files with descriptions]

## API Endpoints Added
[New REST endpoints]

## Test Results
[Test execution results]

## Quality Metrics
[Build status, test coverage, etc.]
```

## Task Delegation Flow

1. **Analysis Phase**:
```
backend-specialist: "Analyze GitHub issue #$ARGUMENTS and create detailed implementation plan for Spring Boot backend. Return architecture design, file changes needed, API specifications, database schema changes, and testing strategy."
```

2. **Implementation Phase**: Parent orchestrates based on analysis
3. **Quality Phase**: Validate and finalize

## Expected Flow
1. backend-specialist → Returns implementation plan
2. Parent receives plan → Implements following the design
3. Call appropriate agents for specific tasks (testing, git, etc.)
4. Deliver production-ready feature

## Quality Standards
- Enterprise Spring Boot practices
- SOLID principles and clean architecture
- Comprehensive testing (90%+ coverage)
- API security and performance
- Proper documentation and git workflow
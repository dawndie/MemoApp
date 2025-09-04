# Backend Issue Command Specification

## Overview
Specification for managing backend issue workflow and task logging requirements.

## Command Workflow

### Phase 1: Issue Analysis
- Review and understand the issue requirements
- Break down complex tasks into manageable subtasks
- Identify potential challenges and implementation strategies

### Phase 2: Implementation
- Follow SOLID principles
- Implement comprehensive business logic
- Write comprehensive tests (unit + integration)

### Phase 3: Quality Assurance
- Run `./gradlew test` to validate all tests pass
- Run `./gradlew build` to ensure compilation success
- Validate against enterprise standards and best practices

### Phase 4: Documentation & Git Management
- Create task specification and workflow action log in `tasks/yyyymmdd_issue[task-content].md`
- Use **git-manager** agent to:
  - Stage all changes
  - Create conventional commit messages base on `tasks/yyyymmdd_[task-content].md` file
  - Push branch to remote
  - Create comprehensive pull request
  - Comment on PR with test results and implementation details

## Task Logging Requirements
- Filename format: `yyyymmdd_issue-[number]_[brief-description].md`
- Include sections:
  1. Issue Reference
  2. Implementation Overview
  3. Technical Details
  4. Test Coverage
  5. Quality Metrics
  6. Potential Future Improvements

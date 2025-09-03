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
Use the **backend-specialist** agent to:
- Fetch and analyze GitHub issue requirements
- Assess technical complexity and scope
- Design architecture and implementation plan
- Return detailed implementation roadmap to parent

### Phase 2: Implementation Orchestration
- get the roadmap from phase 1
- Create branch. for example: feat-1
- Implement changes following analysis
- Write tests

### Phase 3: Quality Assurance
- Run tests
- Validate against enterprise standards
- Ensure comprehensive documentation

### phase 4: Git Management** (git-manager agent)
- Stage changes
- Create conventional commits
- Push branch and create PR

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
---
  allowed-tools: Bash(gh:*), Task, Read, Write, Edit, Grep, Glob
  description: GitHub issue development workflow with sub-agent orchestration
  ---

  # GitHub Issue Development Workflow

  Your task: Get GitHub issue #$ARGUMENTS and develop it through a structured 
  workflow using sub-agents.

  ## Workflow Stages:

  1. **Issue Analysis** (planner-researcher agent)
     - Fetch issue details using `gh issue view $ARGUMENTS`
     - Analyze requirements and scope
     - Break down into implementation tasks

  2. **Code Analysis** (general-purpose agent) 
     - Search codebase for related files
     - Understand existing patterns and architecture
     - Identify files to modify

  3. **Implementation** (Parent agent coordinates)
     - Create feature branch (format: `feat-<issue-number>`)
     - Implement changes following analysis
     - Write tests

  4. **Quality Assurance** (Sub-agents for specific tasks)
     - Run tests and linting
     - Code review and optimization
     - Documentation updates

  5. **Git Management** (git-manager agent)
     - Stage changes
     - Create conventional commits
     - Push branch and create PR
     - Comment on PR with test results and implementation details


  Execute each stage by calling the appropriate sub-agent and waiting for 
  completion before proceeding.
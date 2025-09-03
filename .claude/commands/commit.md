---
description: Commit code changes using git-manager subagent with optional commit message
---
## Context
- Commit message (optional): $ARGUMENTS
- This command will use the git-manager subagent to safely stage, commit, and optionally push code changes

## Your Role
You are a git commit assistant that uses the git-manager subagent to handle code commits with proper conventional commit standards.

## Process
1. **Parse Arguments**: Check if user provided a custom commit message in $ARGUMENTS
2. **Delegate to Git Manager**: Use the git-manager subagent to handle the entire commit process
3. **Report Results**: Provide feedback on the commit operation

## Instructions
When this command is executed:

1. If $ARGUMENTS contains a commit message, pass it as context to the git-manager subagent
2. Use the Task tool with subagent_type "git-manager" to:
   - Stage relevant changes
   - Create a proper commit with conventional commit formatting
   - Include the Claude Code signature
   - Ensure security and professional commit standards

## Task Delegation
Use the git-manager subagent with this prompt structure:

If $ARGUMENTS is provided:
"Commit the current changes with this message context: '$ARGUMENTS'. Follow conventional commit standards and include proper Claude Code signature."

If $ARGUMENTS is empty:
"Analyze the current changes and create an appropriate commit with conventional commit message. Follow professional commit standards and include Claude Code signature."

## Output Format
- **Commit Status**: Success/failure of the commit operation
- **Commit Details**: Hash, message, and files changed
- **Next Steps**: Any recommended follow-up actions

## Important
- Always use the git-manager subagent for safety and consistency
- The subagent will handle staging, commit message formatting, and security checks
- Do not perform git operations directly - delegate everything to git-manager
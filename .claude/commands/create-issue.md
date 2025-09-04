# Create Issue Command

## Usage
```
/create-issue [brief description or feature request]
```

## Description
This command helps you create well-structured issue descriptions and refines your task requirements before saving them to the issues folder. It acts as an interactive issue creation assistant.

## Context
- Repository: MemoApp (Full-stack memo application)
- Frontend: Angular with TypeScript
- Backend: Java Spring Boot
- Current working directory: `/Users/voluongbang/Programing/Java_Learning/MemoApp/MemoApp_Frontend`
- Issues saved to: `/Users/voluongbang/Programing/Java_Learning/MemoApp/issues/`

## Process Flow

### Phase 1: Initial Analysis & Requirements Gathering
When this command is executed:

1. **Parse Input**: Analyze the provided description or feature request from `$ARGUMENTS`
2. **Interactive Refinement**: Ask clarifying questions to gather comprehensive requirements:
   - What type of issue is this? (Feature, Bug, Enhancement, Documentation)
   - Which component of the system is affected? (Frontend, Backend, Full-stack)
   - What is the expected behavior?
   - What are the acceptance criteria?
   - Are there any dependencies or related issues?
   - What is the priority level? (Low, Medium, High, Critical)

### Phase 2: Issue Structure Creation
Generate a well-formatted issue description with the following structure:

```markdown
# Issue Title

## Issue Type
- [ ] Bug Report
- [ ] Feature Request
- [ ] Enhancement
- [ ] Documentation
- [ ] Refactoring

## Component
- [ ] Frontend (Angular)
- [ ] Backend (Spring Boot)
- [ ] Database
- [ ] Full-stack
- [ ] DevOps/CI

## Priority
- [ ] Critical
- [ ] High
- [ ] Medium
- [ ] Low

## Description
[Detailed description of the issue or feature]

## Current Behavior
[What currently happens - for bugs]

## Expected Behavior
[What should happen for bug]

## Steps to Reproduce
[For bugs - detailed steps]

## Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

## Technical Requirements
- [ ] Frontend changes required
- [ ] Backend API changes required
- [ ] Database schema updates needed
- [ ] New dependencies to add

## Implementation Notes
[Technical considerations, architectural decisions, etc.]

## Related Issues
[Link to related issues if any]

## Estimated Effort
- [ ] Small (< 4 hours)
- [ ] Medium (4-16 hours)
- [ ] Large (16-40 hours)
- [ ] Epic (> 40 hours)

## Labels
`[component]` `[priority]` `[type]`
```

### Phase 3: Refinement & Validation
1. **Review the generated issue**: Present the structured issue to the user
2. **Gather feedback**: Ask if any sections need refinement or additional details
3. **Iterate**: Refine the issue based on user feedback
4. **Technical validation**: Ensure the issue is technically feasible and well-defined

### Phase 4: Save & Organize
1. **Generate filename**: Create a descriptive filename based on the issue title
2. **Save to issues folder**: Save the issue as a markdown file in `/Users/voluongbang/Programing/Java_Learning/MemoApp/issues/`
3. **Provide summary**: Give the user a summary of what was created and where it was saved

## File Naming Convention
Issues are saved with the following naming pattern:
- Format: `YYYY-MM-DD-issue-brief-description.md`
- Example: `2024-09-04-feature-user-authentication.md`
- Example: `2024-09-04-bug-memo-deletion-error.md`

## Interactive Questions Template
When refining requirements, ask questions like:
1. "What specific functionality do you want to add/fix?"
2. "Who is the target user for this feature?"
3. "What should happen when [scenario]?"
4. "Are there any edge cases to consider?"
5. "What would make this issue 'done' in your opinion?"
6. "Should this integrate with existing features?"
7. "Any specific UI/UX requirements?"
8. "Performance or security considerations?"

## Integration with Development Workflow
The created issues can be used with existing commands:
- `/frontend-issue` - For frontend implementation
- `/backend-issue` - For backend implementation
- `/develop-issue` - For general development tasks

## Example Usage
```
User: /create-issue Add dark mode toggle to the application
Assistant: I'll help you create a comprehensive issue description for adding a dark mode toggle. Let me ask a few questions to refine the requirements...

[Interactive session follows]

Result: Issue saved to `/Users/voluongbang/Programing/Java_Learning/MemoApp/issues/2024-09-04-feature-dark-mode-toggle.md`
```

## Success Criteria
- ✅ Clear, actionable issue description
- ✅ Well-defined acceptance criteria
- ✅ Appropriate technical requirements identified
- ✅ Priority and effort estimation completed
- ✅ Issue saved to the correct location with proper naming
- ✅ User is satisfied with the level of detail and clarity
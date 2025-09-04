# Add Due Date Functionality to Memos
## Component
- Full-stack

## Description
Add due date functionality to the memo system, allowing users to set optional deadlines for their memos. This feature will help users track time-sensitive tasks and organize their work more effectively.

## Current Behavior
- Memos only have creation and update timestamps
- No way to set deadlines or track when tasks should be completed
- Limited time-based organization capabilities

## Expected Behavior
- Users can optionally set due dates when creating/editing memos
- Visual indicators show overdue memos (red highlighting)
- Memos can be sorted by due date
- Due date information is displayed in memo list and detail views
- Due dates are persisted in the database

## Acceptance Criteria
- [ ] Backend entity supports due date field (LocalDate)
- [ ] Database schema includes due_date column
- [ ] REST API endpoints handle due date in CRUD operations
- [ ] Frontend model includes due date field
- [ ] UI includes date picker for setting due dates
- [ ] Memo list displays due dates with visual indicators
- [ ] Overdue memos are highlighted in red
- [ ] Sorting by due date is available
- [ ] Due dates are optional (can be null/undefined)

## Technical Requirements
- [x] Frontend changes required
- [x] Backend API changes required
- [x] Database schema updates needed
- [ ] New dependencies to add

## Implementation Tasks

### Backend Tasks
1. **Database Schema Update**
   - Add `due_date` column to `memos` table (DATE type, nullable)
   - Create database migration script

2. **Entity Layer**
   - Add `dueDate` field to Memo entity (`LocalDate` type)
   - Update constructors to handle due date parameter
   - Add getter/setter methods

3. **API Layer**
   - Update MemoController to accept due date in requests
   - Modify CreateMemoRequest and UpdateMemoRequest DTOs
   - Add due date filtering/sorting endpoints

4. **Service Layer**
   - Update MemoService to handle due date logic
   - Add method to find overdue memos
   - Implement due date sorting functionality

5. **Repository Layer**
   - Add query methods for due date filtering
   - Create method to find memos by due date range

### Frontend Tasks
6. **Model Updates**
   - Add `dueDate?: string` to Memo interface
   - Update CreateMemoRequest and UpdateMemoRequest interfaces
   - Add due date validation types

7. **UI Components**
   - Add date picker component to memo form
   - Update memo list component to display due dates
   - Add visual indicators for overdue items (red styling)
   - Implement due date sorting in memo list

8. **Service Updates**
   - Update MemoService to handle due date in API calls
   - Add methods for due date filtering/sorting
   - Update HTTP request/response handling

9. **Styling & UX**
   - Add CSS classes for overdue memo highlighting
   - Style date picker component consistently
   - Add icons/indicators for due date status
   - Implement responsive design for date displays

## Implementation Notes
- Use `LocalDate` in backend (date only, no time component)
- Frontend should use HTML5 date input or Angular date picker
- Consider timezone implications (store dates in UTC)
- Due dates should be completely optional
- Overdue check: `dueDate < LocalDate.now()`
- Sort order: overdue first, then by due date ascending, then by created date

## UI Mockup Features
- Date picker in memo creation/edit form
- Due date column in memo list table
- Red background/text for overdue items
- "Due: Sep 15" format in memo cards
- Sort dropdown with "Due Date" option

## Definition of Done
- [ ] All backend API endpoints support due date functionality
- [ ] Database schema updated and migrations applied
- [ ] Frontend UI allows setting and displaying due dates
- [ ] Overdue memos are visually distinct
- [ ] Sorting by due date works correctly
- [ ] All tests pass (unit, integration, E2E)
- [ ] Code review completed
- [ ] Documentation updated
- [ ] Feature tested in staging environment
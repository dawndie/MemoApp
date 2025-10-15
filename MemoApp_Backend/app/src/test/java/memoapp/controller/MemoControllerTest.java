package memoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import memoapp.dto.BulkPriorityUpdateRequest;
import memoapp.dto.PriorityStatistics;
import memoapp.dto.PriorityUpdateRequest;
import memoapp.entity.Memo;
import memoapp.entity.Priority;
import memoapp.exception.MemoNotFoundException;
import memoapp.service.MemoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemoController.class)
class MemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemoService memoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Memo testMemo;

    @BeforeEach
    void setUp() {
        testMemo = new Memo();
        testMemo.setId(1L);
        testMemo.setTitle("Test Memo");
        testMemo.setContent("Test content");
        testMemo.setPriority(Priority.MEDIUM);
        testMemo.setCreatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
        testMemo.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 12, 0));
    }

    @Test
    void getAllMemos_ShouldReturnListOfMemos() throws Exception {
        Memo memo2 = new Memo();
        memo2.setId(2L);
        memo2.setTitle("Second Memo");
        memo2.setContent("Second content");
        memo2.setCreatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));
        memo2.setUpdatedAt(LocalDateTime.of(2024, 1, 2, 12, 0));

        List<Memo> memos = Arrays.asList(testMemo, memo2);
        when(memoService.getAllMemos()).thenReturn(memos);

        mockMvc.perform(get("/api/memos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Memo")))
                .andExpect(jsonPath("$[0].content", is("Test content")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Second Memo")));

        verify(memoService, times(1)).getAllMemos();
    }

    @Test
    void getAllMemos_WhenNoMemos_ShouldReturnEmptyList() throws Exception {
        when(memoService.getAllMemos()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/memos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(memoService, times(1)).getAllMemos();
    }

    @Test
    void getMemoById_WhenMemoExists_ShouldReturnMemo() throws Exception {
        when(memoService.getMemoById(1L)).thenReturn(testMemo);

        mockMvc.perform(get("/api/memos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Memo")))
                .andExpect(jsonPath("$.content", is("Test content")));

        verify(memoService, times(1)).getMemoById(1L);
    }

    @Test
    void getMemoById_WhenMemoDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(memoService.getMemoById(999L)).thenThrow(new MemoNotFoundException(999L));

        mockMvc.perform(get("/api/memos/999"))
                .andExpect(status().isNotFound());

        verify(memoService, times(1)).getMemoById(999L);
    }

    @Test
    void updateMemo_WhenMemoExists_ShouldReturnUpdatedMemo() throws Exception {
        Memo updateRequest = new Memo();
        updateRequest.setTitle("Updated Memo");
        updateRequest.setContent("Updated content");

        Memo updatedMemo = new Memo();
        updatedMemo.setId(1L);
        updatedMemo.setTitle("Updated Memo");
        updatedMemo.setContent("Updated content");

        when(memoService.updateMemo(eq(1L), any(Memo.class))).thenReturn(updatedMemo);

        mockMvc.perform(put("/api/memos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Memo")))
                .andExpect(jsonPath("$.content", is("Updated content")));

        verify(memoService, times(1)).updateMemo(eq(1L), any(Memo.class));
    }

    @Test
    void updateMemo_WhenMemoDoesNotExist_ShouldReturnNotFound() throws Exception {
        Memo updatedMemo = new Memo();
        updatedMemo.setTitle("Updated Memo");
        updatedMemo.setContent("Updated content");

        when(memoService.updateMemo(eq(999L), any(Memo.class))).thenThrow(new MemoNotFoundException(999L));

        mockMvc.perform(put("/api/memos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMemo)))
                .andExpect(status().isNotFound());

        verify(memoService, times(1)).updateMemo(eq(999L), any(Memo.class));
    }

    @Test
    void deleteMemo_ShouldReturnNoContent() throws Exception {
        doNothing().when(memoService).deleteMemo(1L);

        mockMvc.perform(delete("/api/memos/1"))
                .andExpect(status().isNoContent());

        verify(memoService, times(1)).deleteMemo(1L);
    }

    @Test
    void deleteMemo_WithNonExistentId_ShouldStillReturnNoContent() throws Exception {
        doNothing().when(memoService).deleteMemo(999L);

        mockMvc.perform(delete("/api/memos/999"))
                .andExpect(status().isNoContent());

        verify(memoService, times(1)).deleteMemo(999L);
    }

    // ===============================
    // Priority-related Controller Tests
    // ===============================

    @Test
    void getAllMemos_WithPriorityFilter_ShouldReturnFilteredMemos() throws Exception {
        List<Priority> priorities = Arrays.asList(Priority.HIGH, Priority.MEDIUM);
        List<Memo> filteredMemos = Arrays.asList(testMemo);
        
        when(memoService.getMemosByPriority(priorities)).thenReturn(filteredMemos);

        mockMvc.perform(get("/api/memos")
                        .param("priority", "HIGH,MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].priority", is("MEDIUM")));

        verify(memoService, times(1)).getMemosByPriority(priorities);
        verify(memoService, never()).getAllMemos();
    }

    @Test
    void getAllMemos_WithSortParameter_ShouldReturnSortedMemos() throws Exception {
        List<Memo> sortedMemos = Arrays.asList(testMemo);
        
        when(memoService.getMemosSortedByPriority("priority_desc")).thenReturn(sortedMemos);

        mockMvc.perform(get("/api/memos")
                        .param("sort", "priority_desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority", is("MEDIUM")));

        verify(memoService, times(1)).getMemosSortedByPriority("priority_desc");
    }

    @Test
    void updateMemoPriority_WithValidData_ShouldReturnUpdatedMemo() throws Exception {
        PriorityUpdateRequest request = new PriorityUpdateRequest(Priority.HIGH);
        
        Memo updatedMemo = new Memo();
        updatedMemo.setId(1L);
        updatedMemo.setTitle("Test Memo");
        updatedMemo.setContent("Test content");
        updatedMemo.setPriority(Priority.HIGH);
        
        when(memoService.updateMemoPriority(1L, Priority.HIGH)).thenReturn(updatedMemo);

        mockMvc.perform(put("/api/memos/1/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(memoService, times(1)).updateMemoPriority(1L, Priority.HIGH);
    }

    @Test
    void updateMemoPriority_WithInvalidMemo_ShouldReturnNotFound() throws Exception {
        PriorityUpdateRequest request = new PriorityUpdateRequest(Priority.HIGH);
        
        when(memoService.updateMemoPriority(999L, Priority.HIGH))
                .thenThrow(new MemoNotFoundException(999L));

        mockMvc.perform(put("/api/memos/999/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(memoService, times(1)).updateMemoPriority(999L, Priority.HIGH);
    }

    @Test
    void bulkUpdatePriority_WithValidData_ShouldReturnUpdatedMemos() throws Exception {
        List<Long> memoIds = Arrays.asList(1L, 2L, 3L);
        BulkPriorityUpdateRequest request = new BulkPriorityUpdateRequest(memoIds, Priority.HIGH);
        
        Memo memo1 = new Memo("Title 1", "Content 1", Priority.HIGH);
        memo1.setId(1L);
        Memo memo2 = new Memo("Title 2", "Content 2", Priority.HIGH);
        memo2.setId(2L);
        Memo memo3 = new Memo("Title 3", "Content 3", Priority.HIGH);
        memo3.setId(3L);
        
        List<Memo> updatedMemos = Arrays.asList(memo1, memo2, memo3);
        
        when(memoService.bulkUpdatePriority(any(BulkPriorityUpdateRequest.class)))
                .thenReturn(updatedMemos);

        mockMvc.perform(post("/api/memos/bulk/priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].priority", is("HIGH")))
                .andExpect(jsonPath("$[1].priority", is("HIGH")))
                .andExpect(jsonPath("$[2].priority", is("HIGH")));

        verify(memoService, times(1)).bulkUpdatePriority(any(BulkPriorityUpdateRequest.class));
    }

    @Test
    void getPriorityStatistics_ShouldReturnStatistics() throws Exception {
        Map<Priority, Long> priorityCounts = new HashMap<>();
        priorityCounts.put(Priority.HIGH, 3L);
        priorityCounts.put(Priority.MEDIUM, 5L);
        priorityCounts.put(Priority.LOW, 2L);
        priorityCounts.put(Priority.NONE, 1L);

        PriorityStatistics statistics = new PriorityStatistics(priorityCounts, 11L, Priority.MEDIUM);

        when(memoService.getPriorityStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/memos/stats/priority"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalMemos", is(11)))
                .andExpect(jsonPath("$.mostCommonPriority", is("MEDIUM")))
                .andExpect(jsonPath("$.priorityCounts.HIGH", is(3)))
                .andExpect(jsonPath("$.priorityCounts.MEDIUM", is(5)))
                .andExpect(jsonPath("$.priorityCounts.LOW", is(2)))
                .andExpect(jsonPath("$.priorityCounts.NONE", is(1)));

        verify(memoService, times(1)).getPriorityStatistics();
    }

    // ===============================
    // Create Memo Controller Tests (Feature #15)
    // Integration tests for POST /api/memos endpoint
    // ===============================

    @Test
    void createMemo_WithValidData_ShouldReturnCreatedWithMemoResponse() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Integration Test Memo",
                "content": "This is test content for integration testing",
                "priority": "HIGH"
            }
            """;

        Memo createdMemo = new Memo("Integration Test Memo", "This is test content for integration testing", Priority.HIGH);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        createdMemo.setUpdatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

        when(memoService.createMemo(eq("Integration Test Memo"), eq("This is test content for integration testing"), eq(Priority.HIGH)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Integration Test Memo")))
                .andExpect(jsonPath("$.content", is("This is test content for integration testing")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(memoService, times(1)).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithMissingTitleField_ShouldReturnBadRequest() throws Exception {
        // Arrange - Request without title field
        String requestJson = """
            {
                "content": "Content without title",
                "priority": "MEDIUM"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").exists());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithEmptyTitle_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "",
                "content": "Content with empty title",
                "priority": "LOW"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").exists());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithBlankTitle_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "   ",
                "content": "Content with blank title",
                "priority": "MEDIUM"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")));

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithMissingContentField_ShouldReturnBadRequest() throws Exception {
        // Arrange - Request without content field
        String requestJson = """
            {
                "title": "Title without content",
                "priority": "HIGH"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors[?(@.field == 'content')]").exists());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithEmptyContent_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Title with empty content",
                "content": "",
                "priority": "LOW"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors[?(@.field == 'content')]").exists());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithBlankContent_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Title with blank content",
                "content": "   ",
                "priority": "MEDIUM"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")));

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithMissingPriorityField_ShouldReturnBadRequest() throws Exception {
        // Arrange - Request without priority field
        String requestJson = """
            {
                "title": "Title without priority",
                "content": "Content without priority"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors[?(@.field == 'priority')]").exists());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithNullPriority_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Title with null priority",
                "content": "Content with null priority",
                "priority": null
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")));

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithInvalidPriorityValue_ShouldReturnError() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Title with invalid priority",
                "content": "Content with invalid priority",
                "priority": "INVALID_PRIORITY"
            }
            """;

        // Act & Assert
        // Note: Invalid enum values during JSON deserialization cause IllegalArgumentException
        // which is caught by the global exception handler. The handler treats it as a RuntimeException
        // and returns 500 Internal Server Error (as seen in the current implementation).
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError()); // Currently returns 500 due to RuntimeException handler

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithTitleExceedingMaxLength_ShouldReturnBadRequest() throws Exception {
        // Arrange - Create title with 256 characters
        String longTitle = "a".repeat(256);
        String requestJson = String.format("""
            {
                "title": "%s",
                "content": "Content",
                "priority": "MEDIUM"
            }
            """, longTitle);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").exists());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithContentExceedingMaxLength_ShouldReturnBadRequest() throws Exception {
        // Arrange - Create content with 10,001 characters
        String longContent = "a".repeat(10001);
        String requestJson = String.format("""
            {
                "title": "Title",
                "content": "%s",
                "priority": "HIGH"
            }
            """, longContent);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors[?(@.field == 'content')]").exists());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithMultipleValidationErrors_ShouldReturnAllErrors() throws Exception {
        // Arrange - Request with empty title and content
        String requestJson = """
            {
                "title": "",
                "content": "",
                "priority": "LOW"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'content')]").exists())
                .andExpect(jsonPath("$.errorCount", is(2)));

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithAllFieldsMissing_ShouldReturnAllValidationErrors() throws Exception {
        // Arrange - Empty request body
        String requestJson = "{}";

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Error")))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'content')]").exists())
                .andExpect(jsonPath("$.errors[?(@.field == 'priority')]").exists())
                .andExpect(jsonPath("$.errorCount", is(3)));

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_ResponseStructure_ShouldIncludeAllExpectedFields() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Structure Test Memo",
                "content": "Testing response structure",
                "priority": "MEDIUM"
            }
            """;

        Memo createdMemo = new Memo("Structure Test Memo", "Testing response structure", Priority.MEDIUM);
        createdMemo.setId(99L);
        createdMemo.setCreatedAt(LocalDateTime.of(2024, 2, 20, 14, 30, 45));
        createdMemo.setUpdatedAt(LocalDateTime.of(2024, 2, 20, 14, 30, 45));

        when(memoService.createMemo(anyString(), anyString(), any(Priority.class)))
                .thenReturn(createdMemo);

        // Act & Assert - Verify all fields are present in response
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id", is(99)))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.title", is("Structure Test Memo")))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content", is("Testing response structure")))
                .andExpect(jsonPath("$.priority").exists())
                .andExpect(jsonPath("$.priority", is("MEDIUM")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());

        verify(memoService, times(1)).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithLowPriority_ShouldSucceed() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Low Priority Memo",
                "content": "This is a low priority memo",
                "priority": "LOW"
            }
            """;

        Memo createdMemo = new Memo("Low Priority Memo", "This is a low priority memo", Priority.LOW);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(anyString(), anyString(), eq(Priority.LOW)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priority", is("LOW")));

        verify(memoService, times(1)).createMemo(anyString(), anyString(), eq(Priority.LOW));
    }

    @Test
    void createMemo_WithMediumPriority_ShouldSucceed() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Medium Priority Memo",
                "content": "This is a medium priority memo",
                "priority": "MEDIUM"
            }
            """;

        Memo createdMemo = new Memo("Medium Priority Memo", "This is a medium priority memo", Priority.MEDIUM);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(anyString(), anyString(), eq(Priority.MEDIUM)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priority", is("MEDIUM")));

        verify(memoService, times(1)).createMemo(anyString(), anyString(), eq(Priority.MEDIUM));
    }

    @Test
    void createMemo_WithHighPriority_ShouldSucceed() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "High Priority Memo",
                "content": "This is a high priority memo",
                "priority": "HIGH"
            }
            """;

        Memo createdMemo = new Memo("High Priority Memo", "This is a high priority memo", Priority.HIGH);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(anyString(), anyString(), eq(Priority.HIGH)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(memoService, times(1)).createMemo(anyString(), anyString(), eq(Priority.HIGH));
    }

    @Test
    void createMemo_WithNonePriority_ShouldSucceed() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "No Priority Memo",
                "content": "This is a memo with no priority",
                "priority": "NONE"
            }
            """;

        Memo createdMemo = new Memo("No Priority Memo", "This is a memo with no priority", Priority.NONE);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(anyString(), anyString(), eq(Priority.NONE)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.priority", is("NONE")));

        verify(memoService, times(1)).createMemo(anyString(), anyString(), eq(Priority.NONE));
    }

    @Test
    void createMemo_WithSpecialCharactersInTitle_ShouldSucceed() throws Exception {
        // Arrange
        String titleWithSpecialChars = "Test @#$% Memo & Special *Characters*";
        String requestJson = String.format("""
            {
                "title": "%s",
                "content": "Content",
                "priority": "MEDIUM"
            }
            """, titleWithSpecialChars);

        Memo createdMemo = new Memo(titleWithSpecialChars, "Content", Priority.MEDIUM);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(eq(titleWithSpecialChars), anyString(), any(Priority.class)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(titleWithSpecialChars)));

        verify(memoService, times(1)).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithNewlinesInContent_ShouldSucceed() throws Exception {
        // Arrange
        String contentWithNewlines = "Line 1\\nLine 2\\nLine 3";
        String requestJson = """
            {
                "title": "Multiline Content",
                "content": "Line 1\\nLine 2\\nLine 3",
                "priority": "LOW"
            }
            """;

        Memo createdMemo = new Memo("Multiline Content", "Line 1\nLine 2\nLine 3", Priority.LOW);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(anyString(), anyString(), any(Priority.class)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        verify(memoService, times(1)).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithMalformedJson_ShouldReturnError() throws Exception {
        // Arrange - Malformed JSON (missing comma between "content" and "priority")
        String malformedJson = """
            {
                "title": "Test",
                "content": "Content"
                "priority": "HIGH"
            }
            """;

        // Act & Assert
        // Malformed JSON causes HttpMessageNotReadableException which is caught by global handler
        // and returns 500 Internal Server Error (as per current implementation)
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isInternalServerError());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithoutContentType_ShouldReturnError() throws Exception {
        // Arrange
        String requestJson = """
            {
                "title": "Test",
                "content": "Content",
                "priority": "HIGH"
            }
            """;

        // Act & Assert
        // Without content-type, Spring defaults to application/octet-stream which is not supported
        // HttpMediaTypeNotSupportedException is caught by global exception handler
        // Currently returns 500 Internal Server Error (could be improved to return 415)
        mockMvc.perform(post("/api/memos")
                        .content(requestJson))
                .andExpect(status().isInternalServerError());

        verify(memoService, never()).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithTitleAtMaxLength255_ShouldSucceed() throws Exception {
        // Arrange - Title with exactly 255 characters
        String maxLengthTitle = "a".repeat(255);
        String requestJson = String.format("""
            {
                "title": "%s",
                "content": "Content",
                "priority": "HIGH"
            }
            """, maxLengthTitle);

        Memo createdMemo = new Memo(maxLengthTitle, "Content", Priority.HIGH);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(eq(maxLengthTitle), anyString(), any(Priority.class)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(maxLengthTitle)));

        verify(memoService, times(1)).createMemo(anyString(), anyString(), any(Priority.class));
    }

    @Test
    void createMemo_WithContentAtMaxLength10000_ShouldSucceed() throws Exception {
        // Arrange - Content with exactly 10,000 characters
        String maxLengthContent = "a".repeat(10000);
        String requestJson = String.format("""
            {
                "title": "Test",
                "content": "%s",
                "priority": "MEDIUM"
            }
            """, maxLengthContent);

        Memo createdMemo = new Memo("Test", maxLengthContent, Priority.MEDIUM);
        createdMemo.setId(1L);
        createdMemo.setCreatedAt(LocalDateTime.now());
        createdMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(anyString(), eq(maxLengthContent), any(Priority.class)))
                .thenReturn(createdMemo);

        // Act & Assert
        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is(maxLengthContent)));

        verify(memoService, times(1)).createMemo(anyString(), anyString(), any(Priority.class));
    }
}
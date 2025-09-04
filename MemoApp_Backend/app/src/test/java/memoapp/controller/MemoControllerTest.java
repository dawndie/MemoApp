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
    void createMemo_WithValidData_ShouldReturnCreatedMemo() throws Exception {
        Memo newMemo = new Memo();
        newMemo.setTitle("New Memo");
        newMemo.setContent("New content");

        Memo savedMemo = new Memo();
        savedMemo.setId(3L);
        savedMemo.setTitle("New Memo");
        savedMemo.setContent("New content");
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoService.createMemo(any(Memo.class))).thenReturn(savedMemo);

        mockMvc.perform(post("/api/memos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMemo)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.title", is("New Memo")))
                .andExpect(jsonPath("$.content", is("New content")));

        verify(memoService, times(1)).createMemo(any(Memo.class));
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
}
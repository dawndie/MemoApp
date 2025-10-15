package memoapp.service;

import memoapp.dto.BulkPriorityUpdateRequest;
import memoapp.dto.PriorityStatistics;
import memoapp.entity.Memo;
import memoapp.entity.Priority;
import memoapp.exception.MemoNotFoundException;
import memoapp.exception.MemoValidationException;
import memoapp.repository.MemoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test suite for MemoService following SOLID principles.
 * 
 * Tests both positive and negative scenarios, including exception handling,
 * input validation, and business logic correctness.
 */
@ExtendWith(MockitoExtension.class)
class MemoServiceTest {

    @Mock
    private MemoRepository memoRepository;

    @InjectMocks
    private MemoService memoService;

    private Memo testMemo;

    @BeforeEach
    void setUp() {
        testMemo = new Memo();
        testMemo.setId(1L);
        testMemo.setTitle("Test Memo");
        testMemo.setContent("Test content");
        testMemo.setPriority(Priority.MEDIUM);
        testMemo.setCreatedAt(LocalDateTime.now());
        testMemo.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllMemos_ShouldReturnAllMemos() {
        Memo memo2 = new Memo();
        memo2.setId(2L);
        memo2.setTitle("Second Memo");
        memo2.setContent("Second content");
        
        List<Memo> expectedMemos = Arrays.asList(testMemo, memo2);
        when(memoRepository.findAll()).thenReturn(expectedMemos);

        List<Memo> actualMemos = memoService.getAllMemos();

        assertEquals(2, actualMemos.size());
        assertEquals(expectedMemos, actualMemos);
        verify(memoRepository, times(1)).findAll();
    }

    @Test
    void getAllMemos_WhenNoMemos_ShouldReturnEmptyList() {
        when(memoRepository.findAll()).thenReturn(Arrays.asList());

        List<Memo> actualMemos = memoService.getAllMemos();

        assertTrue(actualMemos.isEmpty());
        verify(memoRepository, times(1)).findAll();
    }

    @Test
    void getMemoById_WhenMemoExists_ShouldReturnMemo() {
        when(memoRepository.findById(1L)).thenReturn(Optional.of(testMemo));

        Memo actualMemo = memoService.getMemoById(1L);

        assertNotNull(actualMemo);
        assertEquals(testMemo.getId(), actualMemo.getId());
        assertEquals(testMemo.getTitle(), actualMemo.getTitle());
        assertEquals(testMemo.getContent(), actualMemo.getContent());
        verify(memoRepository, times(1)).findById(1L);
    }

    @Test
    void getMemoById_WhenMemoDoesNotExist_ShouldThrowMemoNotFoundException() {
        when(memoRepository.findById(999L)).thenReturn(Optional.empty());

        MemoNotFoundException exception = assertThrows(MemoNotFoundException.class, () -> 
            memoService.getMemoById(999L));

        assertEquals(999L, exception.getMemoId());
        assertTrue(exception.getMessage().contains("999"));
        verify(memoRepository, times(1)).findById(999L);
    }

    @Test
    void getMemoById_WithNullId_ShouldThrowMemoValidationException() {
        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
            memoService.getMemoById(null));

        assertTrue(exception.getMessage().contains("cannot be null"));
        assertEquals("id", exception.getFieldName());
        verify(memoRepository, never()).findById(any());
    }

    @Test
    void getMemoById_WithNegativeId_ShouldThrowMemoValidationException() {
        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
            memoService.getMemoById(-1L));

        assertTrue(exception.getMessage().contains("positive number"));
        assertEquals("id", exception.getFieldName());
        verify(memoRepository, never()).findById(any());
    }

    @Test
    void updateMemo_WithValidData_ShouldReturnUpdatedMemo() {
        Memo existingMemo = new Memo();
        existingMemo.setId(1L);
        existingMemo.setTitle("Original Title");
        existingMemo.setContent("Original Content");

        Memo updateData = new Memo();
        updateData.setTitle("Updated Title");
        updateData.setContent("Updated Content");

        when(memoRepository.findById(1L)).thenReturn(Optional.of(existingMemo));
        when(memoRepository.save(any(Memo.class))).thenReturn(existingMemo);

        Memo result = memoService.updateMemo(1L, updateData);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        verify(memoRepository, times(1)).findById(1L);
        verify(memoRepository, times(1)).save(existingMemo);
    }

    @Test
    void updateMemo_WhenMemoDoesNotExist_ShouldThrowMemoNotFoundException() {
        Memo updateData = new Memo();
        updateData.setTitle("Updated Title");
        updateData.setContent("Updated Content");

        when(memoRepository.findById(999L)).thenReturn(Optional.empty());

        MemoNotFoundException exception = assertThrows(MemoNotFoundException.class, () -> 
            memoService.updateMemo(999L, updateData));

        assertEquals(999L, exception.getMemoId());
        verify(memoRepository, times(1)).findById(999L);
        verify(memoRepository, never()).save(any());
    }

    @Test
    void deleteMemo_WhenMemoExists_ShouldDeleteMemo() {
        when(memoRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> memoService.deleteMemo(1L));

        verify(memoRepository, times(1)).existsById(1L);
        verify(memoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMemo_WhenMemoDoesNotExist_ShouldThrowMemoNotFoundException() {
        when(memoRepository.existsById(999L)).thenReturn(false);

        MemoNotFoundException exception = assertThrows(MemoNotFoundException.class, () -> 
            memoService.deleteMemo(999L));

        assertEquals(999L, exception.getMemoId());
        verify(memoRepository, times(1)).existsById(999L);
        verify(memoRepository, never()).deleteById(any());
    }

    @Test
    void deleteMemo_WithNullId_ShouldThrowMemoValidationException() {
        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
            memoService.deleteMemo(null));

        assertTrue(exception.getMessage().contains("cannot be null"));
        verify(memoRepository, never()).existsById(any());
        verify(memoRepository, never()).deleteById(any());
    }

    @Test
    void memoExists_WhenMemoExists_ShouldReturnTrue() {
        when(memoRepository.existsById(1L)).thenReturn(true);

        boolean exists = memoService.memoExists(1L);

        assertTrue(exists);
        verify(memoRepository, times(1)).existsById(1L);
    }

    @Test
    void memoExists_WhenMemoDoesNotExist_ShouldReturnFalse() {
        when(memoRepository.existsById(999L)).thenReturn(false);

        boolean exists = memoService.memoExists(999L);

        assertFalse(exists);
        verify(memoRepository, times(1)).existsById(999L);
    }

    // ===============================
    // Priority-related Tests
    // ===============================

    @Test
    void getMemosByPriority_WithValidPriorities_ShouldReturnFilteredMemos() {
        List<Priority> priorities = Arrays.asList(Priority.HIGH, Priority.MEDIUM);
        List<Memo> expectedMemos = Arrays.asList(testMemo);
        
        when(memoRepository.findByPrioritiesOrderByPriorityDescCreatedAtDesc(priorities))
                .thenReturn(expectedMemos);

        List<Memo> result = memoService.getMemosByPriority(priorities);

        assertEquals(1, result.size());
        assertEquals(expectedMemos, result);
        verify(memoRepository, times(1)).findByPrioritiesOrderByPriorityDescCreatedAtDesc(priorities);
    }

    @Test
    void getMemosByPriority_WithNullPriorities_ShouldReturnAllMemos() {
        List<Memo> allMemos = Arrays.asList(testMemo);
        when(memoRepository.findAll()).thenReturn(allMemos);

        List<Memo> result = memoService.getMemosByPriority(null);

        assertEquals(allMemos, result);
        verify(memoRepository, times(1)).findAll();
        verify(memoRepository, never()).findByPrioritiesOrderByPriorityDescCreatedAtDesc(any());
    }

    @Test
    void getMemosByPriority_WithEmptyPriorities_ShouldReturnAllMemos() {
        List<Memo> allMemos = Arrays.asList(testMemo);
        when(memoRepository.findAll()).thenReturn(allMemos);

        List<Memo> result = memoService.getMemosByPriority(new ArrayList<>());

        assertEquals(allMemos, result);
        verify(memoRepository, times(1)).findAll();
    }

    @Test
    void getMemosByPriority_WithOnlyNullValues_ShouldThrowException() {
        List<Priority> priorities = Arrays.asList(null, null);

        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
                memoService.getMemosByPriority(priorities));

        assertTrue(exception.getMessage().contains("At least one valid priority"));
    }

    @Test
    void getMemosSortedByPriority_WithDescendingSort_ShouldReturnSortedMemos() {
        List<Memo> sortedMemos = Arrays.asList(testMemo);
        when(memoRepository.findAllOrderByPriorityDescCreatedAtDesc()).thenReturn(sortedMemos);

        List<Memo> result = memoService.getMemosSortedByPriority("priority_desc");

        assertEquals(sortedMemos, result);
        verify(memoRepository, times(1)).findAllOrderByPriorityDescCreatedAtDesc();
    }

    @Test
    void getMemosSortedByPriority_WithAscendingSort_ShouldReturnSortedMemos() {
        List<Memo> sortedMemos = Arrays.asList(testMemo);
        when(memoRepository.findAllOrderByPriorityAscCreatedAtDesc()).thenReturn(sortedMemos);

        List<Memo> result = memoService.getMemosSortedByPriority("priority_asc");

        assertEquals(sortedMemos, result);
        verify(memoRepository, times(1)).findAllOrderByPriorityAscCreatedAtDesc();
    }

    @Test
    void getMemosSortedByPriority_WithInvalidSort_ShouldThrowException() {
        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
                memoService.getMemosSortedByPriority("invalid_sort"));

        assertTrue(exception.getMessage().contains("Invalid sort order"));
        assertEquals("sort", exception.getFieldName());
    }

    @Test
    void getMemosSortedByPriority_WithNullSort_ShouldReturnAllMemos() {
        List<Memo> allMemos = Arrays.asList(testMemo);
        when(memoRepository.findAll()).thenReturn(allMemos);

        List<Memo> result = memoService.getMemosSortedByPriority(null);

        assertEquals(allMemos, result);
        verify(memoRepository, times(1)).findAll();
    }

    @Test
    void updateMemoPriority_WithValidData_ShouldUpdatePriority() {
        when(memoRepository.findById(1L)).thenReturn(Optional.of(testMemo));
        when(memoRepository.save(any(Memo.class))).thenReturn(testMemo);

        Memo result = memoService.updateMemoPriority(1L, Priority.HIGH);

        assertNotNull(result);
        assertEquals(Priority.HIGH, testMemo.getPriority());
        verify(memoRepository, times(1)).findById(1L);
        verify(memoRepository, times(1)).save(testMemo);
    }

    @Test
    void updateMemoPriority_WithNullPriority_ShouldThrowException() {
        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
                memoService.updateMemoPriority(1L, null));

        assertTrue(exception.getMessage().contains("Priority cannot be null"));
        assertEquals("priority", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void updateMemoPriority_WithNonExistentMemo_ShouldThrowNotFoundException() {
        when(memoRepository.findById(999L)).thenReturn(Optional.empty());

        MemoNotFoundException exception = assertThrows(MemoNotFoundException.class, () -> 
                memoService.updateMemoPriority(999L, Priority.HIGH));

        assertEquals(999L, exception.getMemoId());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void bulkUpdatePriority_WithValidData_ShouldUpdateAllMemos() {
        List<Long> memoIds = Arrays.asList(1L, 2L, 3L);
        BulkPriorityUpdateRequest request = new BulkPriorityUpdateRequest(memoIds, Priority.HIGH);
        
        Memo memo1 = new Memo("Title 1", "Content 1");
        memo1.setId(1L);
        Memo memo2 = new Memo("Title 2", "Content 2");
        memo2.setId(2L);
        Memo memo3 = new Memo("Title 3", "Content 3");
        memo3.setId(3L);
        
        List<Memo> memos = Arrays.asList(memo1, memo2, memo3);

        when(memoRepository.existsById(1L)).thenReturn(true);
        when(memoRepository.existsById(2L)).thenReturn(true);
        when(memoRepository.existsById(3L)).thenReturn(true);
        when(memoRepository.findAllById(memoIds)).thenReturn(memos);
        when(memoRepository.saveAll(memos)).thenReturn(memos);

        List<Memo> result = memoService.bulkUpdatePriority(request);

        assertEquals(3, result.size());
        for (Memo memo : memos) {
            assertEquals(Priority.HIGH, memo.getPriority());
        }
        verify(memoRepository, times(1)).saveAll(memos);
    }

    @Test
    void bulkUpdatePriority_WithNullRequest_ShouldThrowException() {
        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
                memoService.bulkUpdatePriority(null));

        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void bulkUpdatePriority_WithEmptyMemoIds_ShouldThrowException() {
        BulkPriorityUpdateRequest request = new BulkPriorityUpdateRequest(new ArrayList<>(), Priority.HIGH);

        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
                memoService.bulkUpdatePriority(request));

        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    void bulkUpdatePriority_WithTooManyMemos_ShouldThrowException() {
        List<Long> tooManyIds = new ArrayList<>();
        for (long i = 1; i <= 101; i++) {
            tooManyIds.add(i);
        }
        BulkPriorityUpdateRequest request = new BulkPriorityUpdateRequest(tooManyIds, Priority.HIGH);

        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
                memoService.bulkUpdatePriority(request));

        assertTrue(exception.getMessage().contains("more than 100 memos"));
    }

    @Test
    void bulkUpdatePriority_WithNonExistentMemo_ShouldThrowNotFoundException() {
        List<Long> memoIds = Arrays.asList(1L, 999L);
        BulkPriorityUpdateRequest request = new BulkPriorityUpdateRequest(memoIds, Priority.HIGH);

        when(memoRepository.existsById(1L)).thenReturn(true);
        when(memoRepository.existsById(999L)).thenReturn(false);

        MemoNotFoundException exception = assertThrows(MemoNotFoundException.class, () -> 
                memoService.bulkUpdatePriority(request));

        assertEquals(999L, exception.getMemoId());
        verify(memoRepository, never()).saveAll(any());
    }

    @Test
    void getPriorityStatistics_ShouldReturnCorrectStatistics() {
        when(memoRepository.countByPriority(Priority.HIGH)).thenReturn(3L);
        when(memoRepository.countByPriority(Priority.MEDIUM)).thenReturn(5L);
        when(memoRepository.countByPriority(Priority.LOW)).thenReturn(2L);
        when(memoRepository.countByPriority(Priority.NONE)).thenReturn(1L);
        when(memoRepository.count()).thenReturn(11L);

        PriorityStatistics result = memoService.getPriorityStatistics();

        assertNotNull(result);
        assertEquals(11L, result.getTotalMemos());
        assertEquals(Priority.MEDIUM, result.getMostCommonPriority());
        assertEquals(3L, result.getPriorityCounts().get(Priority.HIGH));
        assertEquals(5L, result.getPriorityCounts().get(Priority.MEDIUM));
        assertEquals(2L, result.getPriorityCounts().get(Priority.LOW));
        assertEquals(1L, result.getPriorityCounts().get(Priority.NONE));
    }

    @Test
    void getPriorityStatistics_WithNoCounts_ShouldReturnNoneAsMostCommon() {
        when(memoRepository.countByPriority(any(Priority.class))).thenReturn(0L);
        when(memoRepository.count()).thenReturn(0L);

        PriorityStatistics result = memoService.getPriorityStatistics();

        assertNotNull(result);
        assertEquals(0L, result.getTotalMemos());
        assertEquals(Priority.NONE, result.getMostCommonPriority());
    }

    // ===============================
    // Create Memo Tests (Feature #15)
    // ===============================

    @Test
    void createMemo_WithValidInput_ShouldCreateAndReturnMemo() {
        // Arrange
        String title = "New Memo Title";
        String content = "This is the memo content";
        Priority priority = Priority.HIGH;

        Memo savedMemo = new Memo(title, content, priority);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo(title, content, priority);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(title, result.getTitle());
        assertEquals(content, result.getContent());
        assertEquals(priority, result.getPriority());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithNullTitle_ShouldThrowMemoValidationException() {
        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo(null, "Content", Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("title cannot be null or empty"));
        assertEquals("title", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithEmptyTitle_ShouldThrowMemoValidationException() {
        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo("", "Content", Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("title cannot be null or empty"));
        assertEquals("title", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithBlankTitle_ShouldThrowMemoValidationException() {
        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo("   ", "Content", Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("title cannot be null or empty"));
        assertEquals("title", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithTitleExceeding255Characters_ShouldThrowMemoValidationException() {
        // Arrange - Create a title with 256 characters
        String longTitle = "a".repeat(256);

        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo(longTitle, "Content", Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("cannot exceed 255 characters"));
        assertEquals("title", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithTitleExactly255Characters_ShouldSucceed() {
        // Arrange - Create a title with exactly 255 characters
        String maxLengthTitle = "a".repeat(255);

        Memo savedMemo = new Memo(maxLengthTitle, "Content", Priority.LOW);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo(maxLengthTitle, "Content", Priority.LOW);

        // Assert
        assertNotNull(result);
        assertEquals(maxLengthTitle, result.getTitle());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithNullContent_ShouldThrowMemoValidationException() {
        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo("Title", null, Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("content cannot be null or empty"));
        assertEquals("content", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithEmptyContent_ShouldThrowMemoValidationException() {
        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo("Title", "", Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("content cannot be null or empty"));
        assertEquals("content", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithBlankContent_ShouldThrowMemoValidationException() {
        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo("Title", "   ", Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("content cannot be null or empty"));
        assertEquals("content", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithContentExceeding10000Characters_ShouldThrowMemoValidationException() {
        // Arrange - Create content with 10,001 characters
        String longContent = "a".repeat(10001);

        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo("Title", longContent, Priority.MEDIUM));

        assertTrue(exception.getMessage().contains("cannot exceed 10,000 characters"));
        assertEquals("content", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithContentExactly10000Characters_ShouldSucceed() {
        // Arrange - Create content with exactly 10,000 characters
        String maxLengthContent = "a".repeat(10000);

        Memo savedMemo = new Memo("Title", maxLengthContent, Priority.HIGH);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", maxLengthContent, Priority.HIGH);

        // Assert
        assertNotNull(result);
        assertEquals(maxLengthContent, result.getContent());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithNullPriority_ShouldThrowMemoValidationException() {
        // Act & Assert
        MemoValidationException exception = assertThrows(MemoValidationException.class, () ->
            memoService.createMemo("Title", "Content", null));

        assertTrue(exception.getMessage().contains("Priority cannot be null"));
        assertEquals("priority", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithLowPriority_ShouldSucceed() {
        // Arrange
        Memo savedMemo = new Memo("Title", "Content", Priority.LOW);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", "Content", Priority.LOW);

        // Assert
        assertNotNull(result);
        assertEquals(Priority.LOW, result.getPriority());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithMediumPriority_ShouldSucceed() {
        // Arrange
        Memo savedMemo = new Memo("Title", "Content", Priority.MEDIUM);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", "Content", Priority.MEDIUM);

        // Assert
        assertNotNull(result);
        assertEquals(Priority.MEDIUM, result.getPriority());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithHighPriority_ShouldSucceed() {
        // Arrange
        Memo savedMemo = new Memo("Title", "Content", Priority.HIGH);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", "Content", Priority.HIGH);

        // Assert
        assertNotNull(result);
        assertEquals(Priority.HIGH, result.getPriority());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithNonePriority_ShouldSucceed() {
        // Arrange
        Memo savedMemo = new Memo("Title", "Content", Priority.NONE);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", "Content", Priority.NONE);

        // Assert
        assertNotNull(result);
        assertEquals(Priority.NONE, result.getPriority());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_ShouldSetTimestampsAutomatically() {
        // Arrange
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);

        Memo savedMemo = new Memo("Title", "Content", Priority.MEDIUM);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", "Content", Priority.MEDIUM);

        // Assert
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCreation) || result.getCreatedAt().isEqual(beforeCreation));
        assertTrue(result.getUpdatedAt().isAfter(beforeCreation) || result.getUpdatedAt().isEqual(beforeCreation));
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_ShouldCallRepositorySaveOnce() {
        // Arrange
        Memo savedMemo = new Memo("Title", "Content", Priority.MEDIUM);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        memoService.createMemo("Title", "Content", Priority.MEDIUM);

        // Assert - Verify save was called exactly once
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithSpecialCharactersInTitle_ShouldSucceed() {
        // Arrange
        String titleWithSpecialChars = "Test @#$% Memo & Special *Characters*";

        Memo savedMemo = new Memo(titleWithSpecialChars, "Content", Priority.MEDIUM);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo(titleWithSpecialChars, "Content", Priority.MEDIUM);

        // Assert
        assertNotNull(result);
        assertEquals(titleWithSpecialChars, result.getTitle());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithSpecialCharactersInContent_ShouldSucceed() {
        // Arrange
        String contentWithSpecialChars = "Content with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";

        Memo savedMemo = new Memo("Title", contentWithSpecialChars, Priority.MEDIUM);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", contentWithSpecialChars, Priority.MEDIUM);

        // Assert
        assertNotNull(result);
        assertEquals(contentWithSpecialChars, result.getContent());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithUnicodeCharacters_ShouldSucceed() {
        // Arrange
        String titleWithUnicode = "Test Memo 测试 メモ 테스트";
        String contentWithUnicode = "Unicode content: 你好世界 こんにちは 안녕하세요 🎉🎊";

        Memo savedMemo = new Memo(titleWithUnicode, contentWithUnicode, Priority.MEDIUM);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo(titleWithUnicode, contentWithUnicode, Priority.MEDIUM);

        // Assert
        assertNotNull(result);
        assertEquals(titleWithUnicode, result.getTitle());
        assertEquals(contentWithUnicode, result.getContent());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithNewlinesInContent_ShouldSucceed() {
        // Arrange
        String contentWithNewlines = "Line 1\nLine 2\nLine 3\n\nLine 5";

        Memo savedMemo = new Memo("Title", contentWithNewlines, Priority.MEDIUM);
        savedMemo.setId(1L);
        savedMemo.setCreatedAt(LocalDateTime.now());
        savedMemo.setUpdatedAt(LocalDateTime.now());

        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        // Act
        Memo result = memoService.createMemo("Title", contentWithNewlines, Priority.MEDIUM);

        // Assert
        assertNotNull(result);
        assertEquals(contentWithNewlines, result.getContent());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }
}
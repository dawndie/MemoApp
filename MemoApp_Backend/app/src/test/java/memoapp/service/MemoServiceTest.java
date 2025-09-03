package memoapp.service;

import memoapp.entity.Memo;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    void createMemo_WithValidMemo_ShouldReturnSavedMemo() {
        Memo newMemo = new Memo();
        newMemo.setTitle("New Memo");
        newMemo.setContent("New content");
        
        Memo savedMemo = new Memo();
        savedMemo.setId(3L);
        savedMemo.setTitle("New Memo");
        savedMemo.setContent("New content");
        savedMemo.setCreatedAt(LocalDateTime.now());
        
        when(memoRepository.save(any(Memo.class))).thenReturn(savedMemo);

        Memo result = memoService.createMemo(newMemo);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New Memo", result.getTitle());
        assertEquals("New content", result.getContent());
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    void createMemo_WithNullMemo_ShouldThrowMemoValidationException() {
        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
            memoService.createMemo(null));

        assertTrue(exception.getMessage().contains("cannot be null"));
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithNullTitle_ShouldThrowMemoValidationException() {
        Memo memoWithNullTitle = new Memo();
        memoWithNullTitle.setTitle(null);
        memoWithNullTitle.setContent("Content");

        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
            memoService.createMemo(memoWithNullTitle));

        assertTrue(exception.getMessage().contains("title"));
        assertEquals("title", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithEmptyTitle_ShouldThrowMemoValidationException() {
        Memo memoWithEmptyTitle = new Memo();
        memoWithEmptyTitle.setTitle("   ");
        memoWithEmptyTitle.setContent("Content");

        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
            memoService.createMemo(memoWithEmptyTitle));

        assertTrue(exception.getMessage().contains("title"));
        assertEquals("title", exception.getFieldName());
        verify(memoRepository, never()).save(any());
    }

    @Test
    void createMemo_WithExcessivelyLongTitle_ShouldThrowMemoValidationException() {
        Memo memoWithLongTitle = new Memo();
        memoWithLongTitle.setTitle("a".repeat(256)); // Exceeds 255 character limit
        memoWithLongTitle.setContent("Content");

        MemoValidationException exception = assertThrows(MemoValidationException.class, () -> 
            memoService.createMemo(memoWithLongTitle));

        assertTrue(exception.getMessage().contains("255 characters"));
        assertEquals("title", exception.getFieldName());
        verify(memoRepository, never()).save(any());
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
}
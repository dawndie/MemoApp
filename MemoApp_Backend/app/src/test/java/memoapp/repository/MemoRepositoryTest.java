package memoapp.repository;

import memoapp.entity.Memo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class MemoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemoRepository memoRepository;

    private Memo testMemo;

    @BeforeEach
    void setUp() {
        testMemo = new Memo();
        testMemo.setTitle("Test Memo");
        testMemo.setContent("Test content for repository testing");
    }

    @Test
    void findAll_ShouldReturnAllMemos() {
        Memo savedMemo1 = entityManager.persistAndFlush(testMemo);
        
        Memo memo2 = new Memo();
        memo2.setTitle("Second Memo");
        memo2.setContent("Second memo content");
        Memo savedMemo2 = entityManager.persistAndFlush(memo2);

        List<Memo> memos = memoRepository.findAll();

        assertEquals(2, memos.size());
        assertTrue(memos.stream().anyMatch(m -> m.getId().equals(savedMemo1.getId())));
        assertTrue(memos.stream().anyMatch(m -> m.getId().equals(savedMemo2.getId())));
    }

    @Test
    void findById_WhenMemoExists_ShouldReturnMemo() {
        Memo savedMemo = entityManager.persistAndFlush(testMemo);

        Optional<Memo> foundMemo = memoRepository.findById(savedMemo.getId());

        assertTrue(foundMemo.isPresent());
        assertEquals(savedMemo.getId(), foundMemo.get().getId());
        assertEquals("Test Memo", foundMemo.get().getTitle());
        assertEquals("Test content for repository testing", foundMemo.get().getContent());
        assertNotNull(foundMemo.get().getCreatedAt());
    }

    @Test
    void findById_WhenMemoDoesNotExist_ShouldReturnEmpty() {
        Optional<Memo> foundMemo = memoRepository.findById(999L);

        assertFalse(foundMemo.isPresent());
    }

    @Test
    void save_NewMemo_ShouldPersistWithGeneratedId() {
        Memo savedMemo = memoRepository.save(testMemo);

        assertNotNull(savedMemo.getId());
        assertEquals("Test Memo", savedMemo.getTitle());
        assertEquals("Test content for repository testing", savedMemo.getContent());
        assertNotNull(savedMemo.getCreatedAt());
        assertNotNull(savedMemo.getUpdatedAt());

        Optional<Memo> foundMemo = memoRepository.findById(savedMemo.getId());
        assertTrue(foundMemo.isPresent());
        assertEquals(savedMemo.getId(), foundMemo.get().getId());
    }

    @Test
    void save_UpdateExistingMemo_ShouldUpdateFields() {
        Memo savedMemo = entityManager.persistAndFlush(testMemo);
        LocalDateTime originalCreatedAt = savedMemo.getCreatedAt();
        
        entityManager.detach(savedMemo);
        
        savedMemo.setTitle("Updated Title");
        savedMemo.setContent("Updated Content");
        
        Memo updatedMemo = memoRepository.save(savedMemo);

        assertEquals(savedMemo.getId(), updatedMemo.getId());
        assertEquals("Updated Title", updatedMemo.getTitle());
        assertEquals("Updated Content", updatedMemo.getContent());
        assertEquals(originalCreatedAt, updatedMemo.getCreatedAt());
        assertNotNull(updatedMemo.getUpdatedAt());
    }

    @Test
    void deleteById_ShouldRemoveMemo() {
        Memo savedMemo = entityManager.persistAndFlush(testMemo);
        Long memoId = savedMemo.getId();

        memoRepository.deleteById(memoId);

        Optional<Memo> foundMemo = memoRepository.findById(memoId);
        assertFalse(foundMemo.isPresent());
    }

    @Test
    void deleteById_WithNonExistentId_ShouldNotThrowException() {
        assertDoesNotThrow(() -> memoRepository.deleteById(999L));
    }

    @Test
    void save_ShouldSetTimestampsAutomatically() {
        LocalDateTime beforeSave = LocalDateTime.now().minusSeconds(1);
        
        Memo savedMemo = memoRepository.save(testMemo);
        
        LocalDateTime afterSave = LocalDateTime.now().plusSeconds(1);

        assertNotNull(savedMemo.getCreatedAt());
        assertNotNull(savedMemo.getUpdatedAt());
        assertTrue(savedMemo.getCreatedAt().isAfter(beforeSave));
        assertTrue(savedMemo.getCreatedAt().isBefore(afterSave));
        assertTrue(savedMemo.getUpdatedAt().isAfter(beforeSave));
        assertTrue(savedMemo.getUpdatedAt().isBefore(afterSave));
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        assertEquals(0, memoRepository.count());

        entityManager.persistAndFlush(testMemo);
        assertEquals(1, memoRepository.count());

        Memo memo2 = new Memo();
        memo2.setTitle("Second Memo");
        memo2.setContent("Second content");
        entityManager.persistAndFlush(memo2);
        
        assertEquals(2, memoRepository.count());
    }

    @Test
    void existsById_ShouldReturnCorrectBoolean() {
        Memo savedMemo = entityManager.persistAndFlush(testMemo);

        assertTrue(memoRepository.existsById(savedMemo.getId()));
        assertFalse(memoRepository.existsById(999L));
    }
}
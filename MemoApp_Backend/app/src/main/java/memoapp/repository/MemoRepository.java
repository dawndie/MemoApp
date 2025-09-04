package memoapp.repository;

import memoapp.entity.Memo;
import memoapp.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    
    List<Memo> findByPriorityIn(List<Priority> priorities);
    
    List<Memo> findAllByOrderByPriorityDesc();
    
    List<Memo> findAllByOrderByPriorityAsc();
    
    long countByPriority(Priority priority);
    
    @Query("SELECT m FROM Memo m WHERE m.priority IN :priorities ORDER BY m.priority DESC, m.createdAt DESC")
    List<Memo> findByPrioritiesOrderByPriorityDescCreatedAtDesc(@Param("priorities") List<Priority> priorities);
    
    @Query("SELECT m FROM Memo m ORDER BY " +
           "CASE m.priority " +
           "WHEN memoapp.entity.Priority.HIGH THEN 3 " +
           "WHEN memoapp.entity.Priority.MEDIUM THEN 2 " +
           "WHEN memoapp.entity.Priority.LOW THEN 1 " +
           "WHEN memoapp.entity.Priority.NONE THEN 0 " +
           "END DESC, m.createdAt DESC")
    List<Memo> findAllOrderByPriorityDescCreatedAtDesc();
    
    @Query("SELECT m FROM Memo m ORDER BY " +
           "CASE m.priority " +
           "WHEN memoapp.entity.Priority.HIGH THEN 3 " +
           "WHEN memoapp.entity.Priority.MEDIUM THEN 2 " +
           "WHEN memoapp.entity.Priority.LOW THEN 1 " +
           "WHEN memoapp.entity.Priority.NONE THEN 0 " +
           "END ASC, m.createdAt DESC")
    List<Memo> findAllOrderByPriorityAscCreatedAtDesc();
}
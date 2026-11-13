package com.taskflow.repository;

import com.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByProjectIdOrderByDueDateAsc(Long projectId);
    List<Task> findByStatusIgnoreCaseOrderByDueDateAsc(String status);
    List<Task> findByPriorityIgnoreCaseOrderByDueDateAsc(String priority);
    List<Task> findByDueDateBeforeAndStatusNotIgnoreCaseOrderByDueDateAsc(LocalDate date,String status);
    List<Task> findAllByOrderByCreatedAtDesc();
    long countByStatusIgnoreCase(String status);
    long countByDueDateBeforeAndStatusNotIgnoreCase(LocalDate date,String status);
}

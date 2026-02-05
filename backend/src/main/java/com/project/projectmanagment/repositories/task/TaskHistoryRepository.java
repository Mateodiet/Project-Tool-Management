package com.project.projectmanagment.repositories.task;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.projectmanagment.entities.task.TaskHistory;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByChangedAtDesc(Long taskId);
}

package com.project.projectmanagment.repositories.task;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.projectmanagment.entities.task.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByProjectId(Long projectId);
    List<TaskEntity> findByAssignedTo(Long userId);
    List<TaskEntity> findByTaskStatus(String status);
    List<TaskEntity> findByProjectIdAndTaskStatus(Long projectId, String status);
    void deleteByProjectId(Long projectId);
}

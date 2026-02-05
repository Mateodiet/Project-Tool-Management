package com.project.projectmanagment.repositories.project;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.projectmanagment.entities.project.ProjectEntity;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
    Optional<ProjectEntity> findByProjectName(String projectName);
    boolean existsByProjectName(String projectName);
}

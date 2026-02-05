package com.project.projectmanagment.repositories.project;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.projectmanagment.entities.bridges.ProjectMember;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByUserId(Long userId);
    Optional<ProjectMember> findByUserIdAndProjectId(Long userId, Long projectId);
    List<ProjectMember> findByProjectIdAndStatus(Long projectId, String status);
    void deleteByProjectId(Long projectId);
}

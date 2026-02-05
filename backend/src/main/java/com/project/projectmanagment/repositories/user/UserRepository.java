package com.project.projectmanagment.repositories.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.project.projectmanagment.entities.user.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByUserIdIn(List<Long> userIds);
    boolean existsByEmail(String email);
}

package com.project.projectmanagment.entities.bridges;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_member_tl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "status")
    private String status;

    @Column(name = "joined_at")
    private Date joinedAt;
}

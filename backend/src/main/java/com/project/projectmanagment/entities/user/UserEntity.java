package com.project.projectmanagment.entities.user;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_tl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "is_active")
    private Boolean isActive;
}

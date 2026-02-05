package com.project.projectmanagment.entities.task;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_tl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "task_status")
    private String taskStatus;

    @Column(name = "task_priority")
    private String taskPriority;

    @Column(name = "due_date")
    private Date dueDate;

    @Column(name = "project_id_fk")
    private Long projectId;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}

package com.project.projectmanagment.entities.task;

import java.sql.Timestamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_history_tl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "field_changed")
    private String fieldChanged;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "changed_by")
    private Long changedBy;

    @Column(name = "changed_at")
    private Timestamp changedAt;
}

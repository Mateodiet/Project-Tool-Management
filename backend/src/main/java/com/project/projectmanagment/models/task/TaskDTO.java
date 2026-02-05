package com.project.projectmanagment.models.task;

import java.sql.Date;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private Long taskId;
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private String taskPriority;
    private Date dueDate;
    private Long projectId;
    private String projectName;
    private Long assignedTo;
    private String assignedToName;
    private Long createdBy;
    private Date createdAt;
    private Date updatedAt;
}

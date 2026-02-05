package com.project.projectmanagment.models.task;

import java.sql.Date;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private String taskPriority;
    private Date dueDate;
    private Long projectId;
    private Long assignedTo;
    private Long createdBy;
}

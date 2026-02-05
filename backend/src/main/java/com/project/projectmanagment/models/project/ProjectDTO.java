package com.project.projectmanagment.models.project;

import java.sql.Date;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private Date projectStartDate;
    private String projectStatus;
    private Long createdBy;
    private String creatorEmail;
}

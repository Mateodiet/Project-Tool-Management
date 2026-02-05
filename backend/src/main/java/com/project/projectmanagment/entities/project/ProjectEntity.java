package com.project.projectmanagment.entities.project;

import java.sql.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_tl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", unique = true, nullable = false)
    private String projectName;

    @Column(name = "project_description")
    private String projectDescription;

    @Column(name = "project_start_date")
    private Date projectStartDate;

    @Column(name = "project_status")
    private String projectStatus;

    @Column(name = "project_status_updated_date")
    private Date projectStatusUpdatedDate;

    @Column(name = "task_created_by")
    private Long createdBy;
}

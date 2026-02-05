package com.project.projectmanagment.models.project;

import java.sql.Date;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDTO {
    private Long userId;
    private String email;
    private String name;
    private String role;
    private String status;
    private Date joinedAt;
}

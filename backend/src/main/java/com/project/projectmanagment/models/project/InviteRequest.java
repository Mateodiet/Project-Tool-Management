package com.project.projectmanagment.models.project;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteRequest {
    private String email;
    private String projectName;
    private String role;
    private String invitedBy;
}

package com.project.projectmanagment.models.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String contactNumber;
    private Boolean isActive;
}

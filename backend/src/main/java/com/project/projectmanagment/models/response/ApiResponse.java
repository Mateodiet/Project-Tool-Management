package com.project.projectmanagment.models.response;

import org.springframework.http.HttpStatus;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private HttpStatus status;
    private String message;
    private Object data;

    public static ApiResponse success(String message, Object data) {
        return ApiResponse.builder()
            .status(HttpStatus.OK)
            .message(message)
            .data(data)
            .build();
    }

    public static ApiResponse success(Object data) {
        return success("Success", data);
    }

    public static ApiResponse error(HttpStatus status, String message) {
        return ApiResponse.builder()
            .status(status)
            .message(message)
            .data(null)
            .build();
    }

    public static ApiResponse notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    public static ApiResponse conflict(String message) {
        return error(HttpStatus.CONFLICT, message);
    }

    public static ApiResponse badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiResponse unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiResponse forbidden(String message) {
        return error(HttpStatus.FORBIDDEN, message);
    }
}

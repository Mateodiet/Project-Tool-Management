package com.project.projectmanagment.services;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendProjectInvitation_Success() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendProjectInvitation(
            "user@test.com", "Test Project", "admin@test.com", "/api/project/accept");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendProjectInvitation_Failure() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendProjectInvitation(
            "user@test.com", "Test Project", "admin@test.com", "/api/project/accept");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendTaskAssignmentNotification_Success() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendTaskAssignmentNotification(
            "user@test.com", "Test Task", "Test Project");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendTaskAssignmentNotification_Failure() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendTaskAssignmentNotification(
            "user@test.com", "Test Task", "Test Project");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}

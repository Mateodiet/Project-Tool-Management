package com.project.projectmanagment.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@pmt.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost}")
    private String baseUrl;

    public void sendProjectInvitation(String toEmail, String projectName, String invitedBy, String inviteLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("You've been invited to project: " + projectName);
            message.setText(buildInvitationEmail(projectName, invitedBy, baseUrl + inviteLink));

            mailSender.send(message);
            log.info("Invitation email sent to {} for project '{}'", toEmail, projectName);
        } catch (Exception e) {
            log.error("Failed to send invitation email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendTaskAssignmentNotification(String toEmail, String taskName, String projectName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("New task assigned: " + taskName);
            message.setText(buildTaskAssignmentEmail(taskName, projectName));

            mailSender.send(message);
            log.info("Task assignment email sent to {} for task '{}'", toEmail, taskName);
        } catch (Exception e) {
            log.error("Failed to send task assignment email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildInvitationEmail(String projectName, String invitedBy, String inviteLink) {
        return String.format("""
            Hello,

            You have been invited by %s to join the project "%s".

            To accept this invitation, please click the following link:
            %s

            If you did not expect this invitation, you can ignore this email.

            Best regards,
            PMT Team
            """, invitedBy, projectName, inviteLink);
    }

    private String buildTaskAssignmentEmail(String taskName, String projectName) {
        return String.format("""
            Hello,

            A new task has been assigned to you:

            Task: %s
            Project: %s

            Please log in to PMT to view the task details.

            Best regards,
            PMT Team
            """, taskName, projectName);
    }
}

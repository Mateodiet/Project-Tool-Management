package com.project.projectmanagment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DataInitializer {

    @Value("${spring.jpa.hibernate.ddl-auto:update}")
    private String ddlAuto;

    @Bean
    @Profile("!test")
    public CommandLineRunner initData() {
        return args -> {
            log.info("===========================================");
            log.info("PMT Application Started Successfully");
            log.info("Database DDL Mode: {}", ddlAuto);
            log.info("===========================================");
            log.info("Default test accounts:");
            log.info("  - admin@pmt.com / password123 (Admin)");
            log.info("  - john@pmt.com / password123 (Membre)");
            log.info("  - marie@pmt.com / password123 (Membre)");
            log.info("  - pierre@pmt.com / password123 (Observateur)");
            log.info("===========================================");
        };
    }
}
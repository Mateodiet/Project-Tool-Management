-- PMT Initial Data Seeds
INSERT INTO user_tl (name, email, password, contact_number, created_at, is_active) VALUES
('Admin User', 'admin@pmt.com', 'password123', '0600000001', CURDATE(), true),
('John Developer', 'john@pmt.com', 'password123', '0600000002', CURDATE(), true),
('Marie Designer', 'marie@pmt.com', 'password123', '0600000003', CURDATE(), true),
('Pierre Observer', 'pierre@pmt.com', 'password123', '0600000004', CURDATE(), true)
ON DUPLICATE KEY UPDATE email = email;

-- Projects
INSERT INTO project_tl (project_name, project_description, project_start_date, project_status, task_created_by) VALUES
('E-Commerce Platform', 'Development of a modern e-commerce platform with payment integration', CURDATE(), 'ACTIVE', 1),
('Mobile App V2', 'Complete redesign of the mobile application', DATE_ADD(CURDATE(), INTERVAL -30 DAY), 'ACTIVE', 1),
('API Gateway', 'Centralized API gateway for microservices', DATE_ADD(CURDATE(), INTERVAL -60 DAY), 'COMPLETED', 2)
ON DUPLICATE KEY UPDATE project_name = project_name;

-- Project Members (roles: ADMIN, MEMBRE, OBSERVATEUR)
INSERT INTO project_member_tl (user_id, project_id, role, status, joined_at) VALUES
(1, 1, 'ADMIN', 'ACCEPTED', CURDATE()),
(2, 1, 'MEMBRE', 'ACCEPTED', CURDATE()),
(3, 1, 'MEMBRE', 'ACCEPTED', CURDATE()),
(4, 1, 'OBSERVATEUR', 'ACCEPTED', CURDATE()),
(1, 2, 'ADMIN', 'ACCEPTED', DATE_ADD(CURDATE(), INTERVAL -30 DAY)),
(2, 2, 'MEMBRE', 'ACCEPTED', DATE_ADD(CURDATE(), INTERVAL -28 DAY)),
(3, 2, 'ADMIN', 'ACCEPTED', DATE_ADD(CURDATE(), INTERVAL -25 DAY)),
(2, 3, 'ADMIN', 'ACCEPTED', DATE_ADD(CURDATE(), INTERVAL -60 DAY)),
(1, 3, 'MEMBRE', 'ACCEPTED', DATE_ADD(CURDATE(), INTERVAL -55 DAY))
ON DUPLICATE KEY UPDATE role = role;

-- Tasks for E-Commerce Platform (Project 1)
INSERT INTO task_tl (task_name, task_description, task_status, task_priority, due_date, project_id_fk, assigned_to, created_by, created_at, updated_at) VALUES
('Setup project structure', 'Initialize the project with proper folder structure and dependencies', 'COMPLETED', 'HIGH', DATE_ADD(CURDATE(), INTERVAL -10 DAY), 1, 2, 1, DATE_ADD(CURDATE(), INTERVAL -15 DAY), DATE_ADD(CURDATE(), INTERVAL -10 DAY)),
('Design database schema', 'Create ERD and implement database models', 'COMPLETED', 'HIGH', DATE_ADD(CURDATE(), INTERVAL -5 DAY), 1, 2, 1, DATE_ADD(CURDATE(), INTERVAL -12 DAY), DATE_ADD(CURDATE(), INTERVAL -5 DAY)),
('Implement user authentication', 'JWT-based authentication with refresh tokens', 'IN_PROGRESS', 'HIGH', DATE_ADD(CURDATE(), INTERVAL 5 DAY), 1, 2, 1, DATE_ADD(CURDATE(), INTERVAL -7 DAY), CURDATE()),
('Create product catalog UI', 'Design and implement product listing pages', 'IN_PROGRESS', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 7 DAY), 1, 3, 1, DATE_ADD(CURDATE(), INTERVAL -5 DAY), CURDATE()),
('Integrate Stripe payments', 'Setup Stripe for payment processing', 'TODO', 'HIGH', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 1, 2, 1, DATE_ADD(CURDATE(), INTERVAL -3 DAY), DATE_ADD(CURDATE(), INTERVAL -3 DAY)),
('Shopping cart functionality', 'Implement add/remove/update cart items', 'TODO', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 10 DAY), 1, 2, 1, DATE_ADD(CURDATE(), INTERVAL -2 DAY), DATE_ADD(CURDATE(), INTERVAL -2 DAY)),
('Order management system', 'Create order processing workflow', 'TODO', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 21 DAY), 1, NULL, 1, CURDATE(), CURDATE()),
('Email notifications', 'Setup email templates and notifications', 'TODO', 'LOW', DATE_ADD(CURDATE(), INTERVAL 28 DAY), 1, NULL, 1, CURDATE(), CURDATE());

-- Tasks for Mobile App V2 (Project 2)
INSERT INTO task_tl (task_name, task_description, task_status, task_priority, due_date, project_id_fk, assigned_to, created_by, created_at, updated_at) VALUES
('UI/UX Research', 'Conduct user research and create wireframes', 'COMPLETED', 'HIGH', DATE_ADD(CURDATE(), INTERVAL -20 DAY), 2, 3, 1, DATE_ADD(CURDATE(), INTERVAL -30 DAY), DATE_ADD(CURDATE(), INTERVAL -20 DAY)),
('Design system creation', 'Create reusable components and style guide', 'COMPLETED', 'HIGH', DATE_ADD(CURDATE(), INTERVAL -10 DAY), 2, 3, 1, DATE_ADD(CURDATE(), INTERVAL -25 DAY), DATE_ADD(CURDATE(), INTERVAL -10 DAY)),
('Implement new navigation', 'Bottom tab navigation with animations', 'IN_PROGRESS', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 3 DAY), 2, 2, 1, DATE_ADD(CURDATE(), INTERVAL -8 DAY), CURDATE()),
('Push notifications', 'Firebase push notification integration', 'TODO', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL 15 DAY), 2, 2, 1, CURDATE(), CURDATE()),
('Offline mode', 'Implement offline data sync', 'TODO', 'LOW', DATE_ADD(CURDATE(), INTERVAL 25 DAY), 2, NULL, 1, CURDATE(), CURDATE());

-- Tasks for API Gateway (Project 3 - Completed)
INSERT INTO task_tl (task_name, task_description, task_status, task_priority, due_date, project_id_fk, assigned_to, created_by, created_at, updated_at) VALUES
('Gateway architecture', 'Design the API gateway architecture', 'COMPLETED', 'HIGH', DATE_ADD(CURDATE(), INTERVAL -50 DAY), 3, 2, 2, DATE_ADD(CURDATE(), INTERVAL -60 DAY), DATE_ADD(CURDATE(), INTERVAL -50 DAY)),
('Rate limiting', 'Implement rate limiting per client', 'COMPLETED', 'HIGH', DATE_ADD(CURDATE(), INTERVAL -40 DAY), 3, 2, 2, DATE_ADD(CURDATE(), INTERVAL -55 DAY), DATE_ADD(CURDATE(), INTERVAL -40 DAY)),
('Load balancing', 'Setup load balancing with health checks', 'COMPLETED', 'MEDIUM', DATE_ADD(CURDATE(), INTERVAL -30 DAY), 3, 1, 2, DATE_ADD(CURDATE(), INTERVAL -45 DAY), DATE_ADD(CURDATE(), INTERVAL -30 DAY));

-- Task History examples
INSERT INTO task_history_tl (task_id, field_changed, old_value, new_value, changed_by, changed_at) VALUES
(1, 'taskStatus', 'TODO', 'IN_PROGRESS', 2, DATE_ADD(NOW(), INTERVAL -12 DAY)),
(1, 'taskStatus', 'IN_PROGRESS', 'COMPLETED', 2, DATE_ADD(NOW(), INTERVAL -10 DAY)),
(2, 'taskStatus', 'TODO', 'IN_PROGRESS', 2, DATE_ADD(NOW(), INTERVAL -8 DAY)),
(2, 'taskStatus', 'IN_PROGRESS', 'COMPLETED', 2, DATE_ADD(NOW(), INTERVAL -5 DAY)),
(3, 'taskStatus', 'TODO', 'IN_PROGRESS', 2, DATE_ADD(NOW(), INTERVAL -2 DAY)),
(3, 'assignedTo', NULL, '2', 1, DATE_ADD(NOW(), INTERVAL -7 DAY)),
(4, 'taskPriority', 'LOW', 'MEDIUM', 1, DATE_ADD(NOW(), INTERVAL -3 DAY));
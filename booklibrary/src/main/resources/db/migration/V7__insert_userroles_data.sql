-- Привязка ролей через UserRoles
INSERT INTO UserRoles (user_id, role_id) VALUES
(1, 1), -- Иван — ROLE_USER
(2, 1), -- Елена — ROLE_USER
(3, 1), -- Admin — ROLE_USER
(3, 2); -- Admin — ROLE_ADMIN
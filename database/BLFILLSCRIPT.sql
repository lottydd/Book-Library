-- Очистка таблиц
TRUNCATE TABLE Rental, BookCopies, BookCatalogs, Book, Catalogs, UserRoles, Users, Roles RESTART IDENTITY CASCADE;

-- Роли с указанием id
INSERT INTO Roles (id, roleName) VALUES
(1, 'ROLE_USER'),
(2, 'ROLE_ADMIN');

-- Пользователи с указанием id
INSERT INTO Users (id, username, email, password) VALUES
(1, 'ivan_petrov', 'ivan@example.com', '$2a$10$ClYcRuznHEGgwoU1rIl7IeYZ5NOzNjsykao/vGrxslUS7HUgiSWwK'),
(2, 'elena_ivanova', 'elena@example.com', '$2a$10$4DqD7xeKWzP.6XQlzAinNeyKbU6gruayobf9Gz8rFPIADPwXJjnYe'),
(3, 'admin', 'admin@library.ru', '$2a$10$qCqWZIkvCGtRwRxQya7LAuFaCHF6Zrzrc7V38xj7wjy3yPIeLsapy');

-- Привязка ролей через UserRoles
INSERT INTO UserRoles (user_id, role_id) VALUES
(1, 1), -- Иван — ROLE_USER
(2, 1), -- Елена — ROLE_USER
(3, 1), -- Admin — ROLE_USER
(3, 2); -- Admin — ROLE_ADMIN

-- Каталоги с явным id
INSERT INTO Catalogs (id, name, parent_id) VALUES
(1, 'Фантастика', NULL),
(2, 'Детективы', NULL),
(3, 'Научная литература', NULL),
(4, 'История', NULL),
(5, 'Советская фантастика', 1),
(6, 'Зарубежная фантастика', 1);

-- Книги с явным id
INSERT INTO Book (id, author, bookTitle, isbn, publicationYear, description, storageArrivalDate) VALUES
(1, 'Аркадий и Борис Стругацкие', 'Пикник на обочине', '9785699616224', 1972, 'Классика советской фантастики.', NOW()),
(2, 'Агата Кристи', 'Убийство в Восточном экспрессе', '9785699622355', 1934, 'Известный детектив с Эркюлем Пуаро.', NOW()),
(3, 'Стивен Хокинг', 'Краткая история времени', '9785227029441', 1988, 'Популярная книга о космологии.', NOW()),
(4, 'Джордж Оруэлл', '1984', '9785170441897', 1949, 'Антиутопия о тоталитарном обществе.', NOW()),
(5, 'Ювал Ной Харари', 'Sapiens: Краткая история человечества', '9785040895191', 2011, 'Обзор истории человеческого вида.', NOW());

-- Привязка книг к каталогам с явными id
INSERT INTO BookCatalogs (bookId, catalog_id) VALUES
(1, 5), -- Пикник -> Советская фантастика
(2, 2), -- Восточный экспресс -> Детективы
(3, 3), -- Хокинг -> Научная литература
(4, 6), -- 1984 -> Зарубежная фантастика
(5, 4); -- Харари -> История

-- Копии книг — по 5 AVAILABLE и 1 RENTED с явным copy_id (идут по порядку)
-- Книга 1 — copy_id 1-6
INSERT INTO BookCopies (copy_id, book_id, status) VALUES
(1, 1, 'AVAILABLE'), (2, 1, 'AVAILABLE'), (3, 1, 'AVAILABLE'), (4, 1, 'AVAILABLE'), (5, 1, 'AVAILABLE'),
(6, 1, 'RENTED');

-- Книга 2 — copy_id 7-12
INSERT INTO BookCopies (copy_id, book_id, status) VALUES
(7, 2, 'AVAILABLE'), (8, 2, 'AVAILABLE'), (9, 2, 'AVAILABLE'), (10, 2, 'AVAILABLE'), (11, 2, 'AVAILABLE'),
(12, 2, 'RENTED');

-- Книга 3 — copy_id 13-18
INSERT INTO BookCopies (copy_id, book_id, status) VALUES
(13, 3, 'AVAILABLE'), (14, 3, 'AVAILABLE'), (15, 3, 'AVAILABLE'), (16, 3, 'AVAILABLE'), (17, 3, 'AVAILABLE'),
(18, 3, 'RENTED');

-- Книга 4 — copy_id 19-24
INSERT INTO BookCopies (copy_id, book_id, status) VALUES
(19, 4, 'AVAILABLE'), (20, 4, 'AVAILABLE'), (21, 4, 'AVAILABLE'), (22, 4, 'AVAILABLE'), (23, 4, 'AVAILABLE'),
(24, 4, 'RENTED');

-- Книга 5 — copy_id 25-30
INSERT INTO BookCopies (copy_id, book_id, status) VALUES
(25, 5, 'AVAILABLE'), (26, 5, 'AVAILABLE'), (27, 5, 'AVAILABLE'), (28, 5, 'AVAILABLE'), (29, 5, 'AVAILABLE'),
(30, 5, 'RENTED');

-- Аренды (с явным id), 7-дневные интервалы
INSERT INTO Rental (id, user_id, copy_id, start_date, due_date, return_date, status) VALUES
(1, 1, 6, NOW() - INTERVAL '14 days', NOW() - INTERVAL '7 days', NULL, 'LATE'),
(2, 2, 12, NOW() - INTERVAL '10 days', NOW() - INTERVAL '3 days', NULL, 'LATE'),
(3, 1, 18, NOW() - INTERVAL '20 days', NOW() - INTERVAL '13 days', NOW() - INTERVAL '12 days', 'RETURNED'),
(4, 2, 24, NOW() - INTERVAL '5 days', NOW() + INTERVAL '2 days', NULL, 'RENTED'),
(5, 1, 30, NOW() - INTERVAL '3 days', NOW() + INTERVAL '4 days', NULL, 'RENTED');
-- Аренды (с явным id), 7-дневные интервалы
INSERT INTO Rental (id, user_id, copy_id, start_date, due_date, return_date, rental_status) VALUES
(1, 1, 6, NOW() - INTERVAL '14 days', NOW() - INTERVAL '7 days', NULL, 'LATE'),
(2, 2, 12, NOW() - INTERVAL '10 days', NOW() - INTERVAL '3 days', NULL, 'LATE'),
(3, 1, 18, NOW() - INTERVAL '20 days', NOW() - INTERVAL '13 days', NOW() - INTERVAL '12 days', 'RETURNED'),
(4, 2, 24, NOW() - INTERVAL '5 days', NOW() + INTERVAL '2 days', NULL, 'RENTED'),
(5, 1, 30, NOW() - INTERVAL '3 days', NOW() + INTERVAL '4 days', NULL, 'RENTED');
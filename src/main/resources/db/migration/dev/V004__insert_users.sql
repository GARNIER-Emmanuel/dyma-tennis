INSERT INTO public.dyma_user (login, password, last_name, first_name)
VALUES
    ('admin', '$2y$10$ctAvNSAv16cnhSRLGWcj.eYA8fSFcpI.aFPQmg.HN9ZmO0hiCYNty', 'admin_LastName', 'Dyma_Admin'),
    ('user', '$2y$10$xWkZ30VWLB9MeZJ6nR5rBuitpp4WUOOGw6Wf74Avv4y0EBkW3CNLa', 'user_LastName', 'Dyma_User');

INSERT INTO public.dyma_user_role (user_id, role_name)
VALUES
    (1, 'ROLE_ADMIN'),
    (1, 'ROLE_USER'),
    (2, 'ROLE_USER');
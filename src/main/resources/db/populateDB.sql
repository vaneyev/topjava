DELETE
FROM meals;
DELETE
FROM user_roles;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, datetime, description, calories)
VALUES (100000, '2020-10-19 8:30', 'User Breakfast', 500),
       (100000, '2020-10-19 12:35', 'User Lunch', 1000),
       (100000, '2020-10-19 18:10', 'User Dinner', 1001),
       (100000, '2020-10-20 8:30', 'User Breakfast', 500),
       (100000, '2020-10-20 12:35', 'User Lunch', 500),
       (100000, '2020-10-20 18:10', 'User Dinner', 1000),
       (100001, '2020-10-20 8:30', 'Admin Breakfast', 500),
       (100001, '2020-10-20 12:35', 'Admin Lunch', 500),
       (100001, '2020-10-20 18:10', 'Admin Dinner', 1000);

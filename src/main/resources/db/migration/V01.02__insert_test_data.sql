INSERT into ADDRESS(state, country, town, street, house_number)
    values('Czech Republic', 'Czech Republic', 'Chlumany', '', 123);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('USA', 'California', 'Los Angeles', 'Hollywood Boulevard', 654);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('Gernamy', 'Gernamy', 'Passau', 'Street', 4);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('Czech Republic', 'Czech Republic', 'Chlumany', 'none', 123);
INSERT into ADDRESS(state, country, town, street, house_number)
values('', '', '', '', 1);

INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Martin', 'Lácha', CURRENT_DATE, 1);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Vlastimil', 'Lácha', CURRENT_DATE, 2);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Jan', 'Lácha', CURRENT_DATE, 3);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Charon', 'Janeček', CURRENT_DATE, 4);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
values ('Admin', '', CURRENT_DATE, 5);

INSERT into AUTH_ROLE (role_name) values ('SUPER_ADMIN');
INSERT into AUTH_ROLE (role_name) values ('ADMIN');
INSERT into AUTH_ROLE (role_name) values ('USER');

INSERT into AUTH_USER(email, password, user_info_id, new_account)
values('admin@community.com', '$2a$12$YdOuL7mvd1iLv.FlfOw4tuLHBGbKlylLF.hWWUY7ASxXDdHfX9Y8a', 5, false);
INSERT into AUTH_USER(email, password, user_info_id, new_account)
    values('martin.lacha@seznam.cz', '$2a$12$YdOuL7mvd1iLv.FlfOw4tuLHBGbKlylLF.hWWUY7ASxXDdHfX9Y8a', 1, false);
INSERT into AUTH_USER(email, password, user_info_id, new_account)
    values('vlastimil.lacha@gmail.com', '$2a$12$YdOuL7mvd1iLv.FlfOw4tuLHBGbKlylLF.hWWUY7ASxXDdHfX9Y8a', 2, false);
INSERT into AUTH_USER(email, password, user_info_id, new_account)
    values('random_email@seznam.cz', '$2a$12$YdOuL7mvd1iLv.FlfOw4tuLHBGbKlylLF.hWWUY7ASxXDdHfX9Y8a', 3, false);
INSERT into AUTH_USER(email, password, user_info_id, new_account)
    values('testing@gmail.com', '$2a$12$YdOuL7mvd1iLv.FlfOw4tuLHBGbKlylLF.hWWUY7ASxXDdHfX9Y8a', 4, false);

INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (1, 1);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (1, 2);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (1, 3);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (2, 2);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (2, 3);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (3, 3);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (4, 3);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (5, 2);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (5, 3);

INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 1, 2);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 3, 1);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 1, 4);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 2, 3);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('BLOCKED', 2, 4);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('BLOCKED', 3, 4);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('Czech Republic', 'Czech Republic', 'Chlumany', '', 123);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('USA', 'California', 'Los Angeles', 'Hollywood Boulevard', 654);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('Gernamy', 'Gernamy', 'Passau', 'Street', 4);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('Czech Republic', 'Czech Republic', 'Chlumany', 'none', 123);

INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Martin', 'Lácha', CURRENT_DATE, 1);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Vlastimil', 'Lácha', CURRENT_DATE, 2);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Jan', 'Lácha', CURRENT_DATE, 3);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Charon', 'Janeček', CURRENT_DATE, 4);

INSERT into AUTH_ROLE (role_name) values ('ADMIN');
INSERT into AUTH_ROLE (role_name) values ('USER');

INSERT into AUTH_USER(email, password, user_info_id)
    values('martin.lacha@seznam.cz', '$2a$10$KA2xCDfloVWE8pSyjiIx5eg8f6v7KGUqRWNu6yEVA7.OtBWPuMAL2', 1);
INSERT into AUTH_USER(email, password, user_info_id)
    values('vlastimil.lacha@gmail.com', 'password', 2);
INSERT into AUTH_USER(email, password, user_info_id)
    values('random_email@seznam.cz', '$2a$10$KA2xCDfloVWE8pSyjiIx5eg8f6v7KGUqRWNu6yEVA7.OtBWPuMAL2', 3);
INSERT into AUTH_USER(email, password, user_info_id)
    values('temporaryAccount@gmail.com', 'password', 4);
INSERT into AUTH_USER(email, password, user_info_id)
    values('n@s', '$2a$10$1Ubym8iB9sRrXU36JC9etun11siJ4/qURUhYfM2t4.9j9beReewxS', 2);

INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (1, 1);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (1, 2);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (2, 1);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (2, 2);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (3, 2);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (4, 2);
INSERT INTO AUTH_USER_ROLE (user_id, role_id) VALUES (5, 2);

INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 1, 2);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 3, 1);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 1, 4);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('FRIEND', 2, 3);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('BLOCKED', 2, 4);
INSERT into FRIENDSHIP(status, sender_id, receiver_id) values('BLOCKED', 3, 4);
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

INSERT into AUTH_USER(email, password, user_info_id, role_id)
    values('martin.lacha@seznam.cz', '$2a$10$1Ubym8iB9sRrXU36JC9etu1mDy/mlIhlW1sJI8v7EhgTc5T5bOhJS', 1, 2);
INSERT into AUTH_USER(email, password, user_info_id, role_id)
    values('vlastimil.lacha@gmail.com', 'password', 2, 2);
INSERT into AUTH_USER(email, password, user_info_id, role_id)
    values('random_email@seznam.cz', '$2a$10$1Ubym8iB9sRrXU36JC9etu1mDy/mlIhlW1sJI8v7EhgTc5T5bOhJS', 3, 2);
INSERT into AUTH_USER(email, password, user_info_id, role_id)
    values('temporaryAccount@gmail.com', 'password', 4, 2);
INSERT into AUTH_USER(email, password, role_id)
    values('newUser@gmail.com', 'password', 2);
INSERT into ADDRESS(state, country, town, street, house_number)
    values('Czech Republic', 'Czech Republic', 'Chlumany', '38422', '', 123);
INSERT into ADDRESS(state, country, town, street, house_number)
            values('USA', 'California', 'Los Angeles', '123456', 'Hollywood Boulevard', 654);

INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Martin', 'Lacha', CURRENT_DATE, 1);
INSERT into USER_INFO(name, surname, date_of_birth, address_id)
    values ('Vlastimil', 'Lácha', CURRENT_DATE, 2);

INSERT into AUTH_ROLE (role_name) values ('ADMIN');
INSERT into AUTH_ROLE (role_name) values ('USER');

INSERT into AUTH_USER(email, password, user_info_id, role_id)
values('martin.lacha@seznam.cz', '$2a$10$1Ubym8iB9sRrXU36JC9etu1mDy/mlIhlW1sJI8v7EhgTc5T5bOhJS', 1, 2);
INSERT into AUTH_USER(email, password, user_info_id, role_id)
values('vlastimil.lacha@gmail.com', 'password', 2, 2);
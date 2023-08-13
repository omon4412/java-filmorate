--insert into "user" ("email", "login", "name", "birthday")
--values ('email@email.ru', 'login', 'name', '2000-12-12');
select * from "user";

insert into "mpa_rating" ("name") values ( 'G' ),
                                         ('PG'),
                                         ('PG-13'),
                                         ('R'),
                                         ('NC-17');

insert into "genre" ("name") values ( 'Комедия' ),
                                    ('Драма'),
                                    ('Мультфильм'),
                                    ('Триллер'),
                                    ('Документальный'),
                                    ('Боевик');

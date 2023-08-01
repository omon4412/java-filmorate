CREATE TABLE IF NOT EXISTS "film" (
  "film_id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar,
  "release_date" date NOT NULL,
  "duration" integer NOT NULL,
  "mpa_rating_id" integer
);

CREATE TABLE IF NOT EXISTS "user" (
  "user_id" integer PRIMARY KEY,
  "email" varchar NOT NULL,
  "login" varchar NOT NULL,
  "name" varchar,
  "birthday" date NOT NULL
);

CREATE TABLE IF NOT EXISTS "mpa_rating" (
  "mpa_rating_id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE IF NOT EXISTS "genre" (
  "genre_id" integer PRIMARY KEY,
  "name" varchar
);

CREATE TABLE IF NOT EXISTS "film_genre" (
  "film_genre_id" integer PRIMARY KEY,
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE IF NOT EXISTS "film_user_like" (
  "film_user_like_id" integer PRIMARY KEY,
  "user_id" integer,
  "film_id" integer
);

CREATE TABLE IF NOT EXISTS "user_friend" (
  "user_friend_id" integer PRIMARY KEY,
  "user_id_1" integer,
  "user_id_2" integer,
  "status" boolean
);

ALTER TABLE "film" ADD FOREIGN KEY ("mpa_rating_id") REFERENCES "mpa_rating" ("mpa_rating_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("genre_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("film_id");

ALTER TABLE "film_user_like" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("user_id");

ALTER TABLE "film_user_like" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("film_id");

ALTER TABLE "user_friend" ADD FOREIGN KEY ("user_id_1") REFERENCES "user" ("user_id");

ALTER TABLE "user_friend" ADD FOREIGN KEY ("user_id_2") REFERENCES "user" ("user_id");

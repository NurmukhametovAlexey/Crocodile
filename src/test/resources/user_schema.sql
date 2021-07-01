drop table if exists "User";
CREATE TABLE "User" (
                        login varchar PRIMARY KEY,
                        password varchar NOT NULL,
                        email varchar NOT NULL UNIQUE,
                        role varchar(10) NOT NULL,
                        score int NOT NULL,

                        enabled boolean DEFAULT true
);
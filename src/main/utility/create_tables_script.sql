CREATE TABLE Dictionary (
                            word varchar(20)  PRIMARY KEY NOT NULL ,
                            difficulty int NOT NULL
);

CREATE SEQUENCE user_id_seq;

CREATE TABLE "User" (
                        userId int PRIMARY KEY NOT NULL DEFAULT nextval('user_id_seq'),
                        login varchar NOT NULL UNIQUE,
                        password varchar NOT NULL,
                        email varchar NOT NULL UNIQUE,
                        role varchar(10) NOT NULL,
                        score int,

                        enabled boolean DEFAULT true
);

ALTER SEQUENCE user_id_seq OWNED BY "User".userId;

CREATE TYPE gameStatus AS ENUM ('NEW', 'IN_PROGRESS', 'FINISHED','CANCELLED');

CREATE TABLE Game (
                      gameUUID varchar(36) PRIMARY KEY NOT NULL,
                      word varchar(20) NOT NULL REFERENCES Dictionary,
                      timeStarted timestamp,
                      timeFinished timestamp,
                      status gameStatus
);

CREATE TYPE playerStatus AS ENUM ('PAINTER', 'GUESSER','PAINTER_WINNER', 'GUESSER_WINNER');

CREATE TABLE GameUser (
                          userId int REFERENCES "User",
                          gameUUID varchar(36) REFERENCES Game,
                          playerRole playerStatus,
                          PRIMARY KEY (userId, gameUUID)
);

CREATE CAST (character varying AS gamestatus) WITH INOUT AS ASSIGNMENT;
CREATE CAST (character varying AS playerstatus) WITH INOUT AS ASSIGNMENT;


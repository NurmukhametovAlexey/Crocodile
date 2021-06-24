CREATE TABLE Dictionary (
                            word varchar(20)  PRIMARY KEY NOT NULL ,
                            difficulty int NOT NULL
);

CREATE TABLE "User" (
                        login varchar PRIMARY KEY,
                        password varchar NOT NULL,
                        email varchar NOT NULL UNIQUE,
                        role varchar(10) NOT NULL,
                        score int,

                        enabled boolean DEFAULT true
);

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
                          login varchar REFERENCES "User",
                          gameUUID varchar(36) REFERENCES Game,
                          playerRole playerStatus,
                          PRIMARY KEY (login, gameUUID)
);

CREATE TABLE Chat (
                      message varchar,
                      login varchar REFERENCES "User",
                      gameUUID varchar(36) REFERENCES Game,
                      timeSent timestamp,
                      PRIMARY KEY (login, gameUUID, timeSent)
);

CREATE TABLE Canvas (
                      picture bytea,
                      gameUUID varchar(36) REFERENCES Game PRIMARY KEY
);

CREATE CAST (character varying AS gamestatus) WITH INOUT AS ASSIGNMENT;
CREATE CAST (character varying AS playerstatus) WITH INOUT AS ASSIGNMENT;


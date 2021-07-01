drop table if exists GameUser cascade;
drop type if exists playerStatus;

CREATE TYPE playerStatus AS ENUM ('PAINTER', 'GUESSER');

CREATE TABLE GameUser (
                          login varchar REFERENCES "User",
                          gameUUID varchar(36) REFERENCES Game,
                          playerRole playerStatus,
                          PRIMARY KEY (login, gameUUID)
);
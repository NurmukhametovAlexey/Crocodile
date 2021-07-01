drop table if exists Game;
drop type if exists gameStatus;

CREATE TYPE gameStatus AS ENUM ('NEW', 'IN_PROGRESS', 'FINISHED','CANCELLED');

CREATE TABLE Game (
                      gameUUID varchar(36) PRIMARY KEY NOT NULL,
                      word varchar(20) NOT NULL REFERENCES Dictionary,
                      timeStarted timestamp,
                      timeFinished timestamp,
                      status gameStatus
);
drop table if exists Chat cascade;

CREATE TABLE Chat (
                      message varchar,
                      login varchar REFERENCES "User",
                      gameUUID varchar(36) REFERENCES Game,
                      timeSent timestamp,
                      PRIMARY KEY (login, gameUUID, timeSent)
);
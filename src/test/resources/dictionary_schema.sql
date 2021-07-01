drop table if exists Dictionary cascade;
CREATE TABLE Dictionary (
                            word varchar(20)  PRIMARY KEY NOT NULL ,
                            difficulty int NOT NULL
);
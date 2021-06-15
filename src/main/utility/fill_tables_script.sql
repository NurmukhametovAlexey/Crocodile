INSERT INTO "User"(login, password, email, role, score)  VALUES (
                                                                    'Bob', 'Bob', 'Bob.mail.ru', 'User', 0
                                                                );
INSERT INTO "User"(login, password, email, role, score)  VALUES (
                                                                    'Alice', 'Alice', 'Alice.mail.ru', 'Admin', 0
                                                                );
INSERT INTO "User"(login, password, email, role, score)  VALUES (
                                                                    'John', 'John', 'John.mail.ru', 'User', 0
                                                                );

---------------------------------

INSERT INTO dictionary(word, difficulty) VALUES (
                                                    'potato', 1
                                                );
INSERT INTO dictionary(word, difficulty) VALUES (
                                                    'apple', 2
                                                );
INSERT INTO dictionary(word, difficulty) VALUES (
                                                    'book', 1
                                                );

---------------------------------

INSERT INTO Game(gameuuid, word, status) VALUES (
                                                          'qwe-rty-123', 'potato', 'NEW'
                                                      );
INSERT INTO Game(gameuuid, word, status) VALUES (
                                                          'aaa-bbb-123', 'apple', 'IN_PROGRESS'
                                                      );
INSERT INTO Game(gameuuid, word, status) VALUES (
                                                          '123-123-123', 'book', 'FINISHED'
                                                      );

---------------------------------

INSERT INTO gameuser(userid, gameuuid, playerrole) VALUES (
                                                              1, 'qwe-rty-123', 'PAINTER'
                                                          );
INSERT INTO gameuser(userid, gameuuid, playerrole) VALUES (
                                                              2, 'qwe-rty-123', 'GUESSER'
                                                          );
INSERT INTO gameuser(userid, gameuuid, playerrole) VALUES (
                                                              2, 'aaa-bbb-123', 'PAINTER'
                                                          );
INSERT INTO gameuser(userid, gameuuid, playerrole) VALUES (
                                                              3, 'aaa-bbb-123', 'GUESSER'
                                                          );


INSERT INTO "User"(login, password, email, role, score) VALUES ('t', 't', 't', 't', 0),
                                                               ('e', 'e', 'e', 'e', 0),
                                                               ('r', 'r', 'r', 'r', 0);

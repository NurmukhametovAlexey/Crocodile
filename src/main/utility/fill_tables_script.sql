create temporary table t1 (w varchar(20));
copy t1 FROM 'D:\Programming\IdeaProjects\Crocodile\src\main\utility\difficulty_1_words.txt';
insert into dictionary(word, difficulty) select w, 1 from t1 ON CONFLICT DO NOTHING;
drop table t1;

create temporary table t2 (w varchar(20));
copy t2 FROM 'D:\Programming\IdeaProjects\Crocodile\src\main\utility\difficulty_2_words.txt';
insert into dictionary(word, difficulty) select w, 2 from t2 ON CONFLICT DO NOTHING;
drop table t2;

create temporary table t3 (w varchar(20));
copy t3 FROM 'D:\Programming\IdeaProjects\Crocodile\src\main\utility\difficulty_3_words.txt';
insert into dictionary(word, difficulty) select w, 3 from t3 ON CONFLICT DO NOTHING;
drop table t3;

create temporary table t10 (w varchar(20));
copy t10 FROM 'D:\Programming\IdeaProjects\Crocodile\src\main\utility\difficulty_10_words.txt';
insert into dictionary(word, difficulty) select w, 10 from t10 ON CONFLICT DO NOTHING;
drop table t10;


create temporary table rus (tmp1 int, w varchar, tmp2 real, tmp3 int);
copy rus FROM 'D:\Programming\IdeaProjects\Crocodile\src\main\utility\russian_words.txt'  DELIMITER ',';
insert into dictionary(word, difficulty) select w, 4 from rus ON CONFLICT DO NOTHING;
drop table rus;
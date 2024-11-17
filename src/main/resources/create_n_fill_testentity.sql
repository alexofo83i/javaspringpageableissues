create table testentity(
  id int, name text
);


insert into testentity
with generator as
 ( select a.*, row_number() over()  as id
     from generate_series ( 1, 1000000 ) a
    order by random()
 )
 select a.id as id
      , substr( md5( random()::text ), 0, 20 )  as name
 from generator a;  


select * from testentity;
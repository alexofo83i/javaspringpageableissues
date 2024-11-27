explain (analyze, buffers, costs off) execute query(10,10);
deallocate query;
prepare query(int,int) as
select t.id, t.name, count(*) over() as total_cnt from testentity t order by t.name limit $1 offset $2;
--QUERY PLAN                                                                                  |
----------------------------------------------------------------------------------------------+
--Limit (actual time=1047.970..1047.974 rows=10 loops=1)                                      |
--  Buffers: shared hit=6373, temp read=8105 written=4151                                     |
--  ->  Sort (actual time=1047.965..1047.969 rows=20 loops=1)                                 |
--        Sort Key: name                                                                      |
--        Sort Method: top-N heapsort  Memory: 27kB                                           |
--        Buffers: shared hit=6373, temp read=8105 written=4151                               |
--        ->  WindowAgg (actual time=773.197..899.145 rows=1000000 loops=1)                   |
--              Buffers: shared hit=6370, temp read=8105 written=4151                         |
--              ->  Seq Scan on testentity t (actual time=0.133..197.673 rows=1000000 loops=1)|
--                    Buffers: shared hit=6370                                                |
--Planning:                                                                                   |
--  Buffers: shared hit=51                                                                    |
--Planning Time: 0.671 ms                                                                     |
--Execution Time: 1048.490 ms                                                                 |

drop index testentity_name_idx;
create index testentity_name_idx on testentity using btree (name);

explain (analyze, buffers, costs off) execute query(10,1000);
deallocate query;
prepare query(int,int) as
select t.id, t.name, count(*) over() as total_cnt from testentity t order by t.name limit $1 offset $2;

--QUERY PLAN                                                                                                        |
--------------------------------------------------------------------------------------------------------------------+
--Limit (actual time=1158.356..1158.360 rows=10 loops=1)                                                            |
--  Buffers: shared hit=999846 read=4929, temp read=3959 written=4151                                               |
--  ->  WindowAgg (actual time=1158.139..1158.315 rows=1010 loops=1)                                                |
--        Buffers: shared hit=999846 read=4929, temp read=3959 written=4151                                         |
--        ->  Index Scan using testentity_name_idx on testentity t (actual time=0.025..876.162 rows=1000000 loops=1)|
--              Buffers: shared hit=999846 read=4929                                                                |
--Planning:                                                                                                         |
--  Buffers: shared hit=16 read=1                                                                                   |
--Planning Time: 0.373 ms                                                                                           |
--Execution Time: 1169.343 ms                                                                                       |

drop index testentity_name_idx;
create index testentity_name_idx on testentity using btree (name, id);

explain (analyze, buffers, costs off) execute query(10,1000);
deallocate query;
prepare query(int,int) as
select t.id, t.name, count(*) over() as total_cnt from testentity t order by t.name limit $1 offset $2;


--QUERY PLAN                                                                                                             |
-------------------------------------------------------------------------------------------------------------------------+
--Limit (actual time=606.922..606.926 rows=10 loops=1)                                                                   |
--  Buffers: shared hit=5095, temp read=3959 written=4151                                                                |
--  ->  WindowAgg (actual time=606.728..606.886 rows=1010 loops=1)                                                       |
--        Buffers: shared hit=5095, temp read=3959 written=4151                                                          |
--        ->  Index Only Scan using testentity_name_idx on testentity t (actual time=0.042..246.234 rows=1000000 loops=1)|
--              Heap Fetches: 314                                                                                        |
--              Buffers: shared hit=5095                                                                                 |
--Planning Time: 0.139 ms                                                                                                |
--Execution Time: 616.400 ms                                                                                             |

drop index testentity_name_idx;
create index testentity_name_idx on testentity using btree (name) include ( id);

explain (analyze, buffers, costs off) execute query(10,1000);
deallocate query;
prepare query(int,int) as
select t.id, t.name, count(*) over() as total_cnt from testentity t order by t.name limit $1 offset $2;

--QUERY PLAN                                                                                                             |
-------------------------------------------------------------------------------------------------------------------------+
--Limit (actual time=331.228..331.231 rows=10 loops=1)                                                                   |
--  Buffers: shared hit=166 read=4929, temp read=3959 written=4151                                                       |
--  ->  WindowAgg (actual time=331.069..331.201 rows=1010 loops=1)                                                       |
--        Buffers: shared hit=166 read=4929, temp read=3959 written=4151                                                 |
--        ->  Index Only Scan using testentity_name_idx on testentity t (actual time=0.029..117.762 rows=1000000 loops=1)|
--              Heap Fetches: 314                                                                                        |
--              Buffers: shared hit=166 read=4929                                                                        |
--Planning:                                                                                                              |
--  Buffers: shared hit=19 read=1                                                                                        |
--Planning Time: 0.326 ms                                                                                                |
--Execution Time: 338.139 ms                                                                                             |
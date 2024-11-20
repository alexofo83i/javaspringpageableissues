package ru.fedorov.querytest.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import ru.fedorov.querytest.entity.MyEntity;

public interface MyQueryRepository extends PagingAndSortingRepository<MyEntity, Long>
                                           , CrudRepository<MyEntity, Long> {
        Page<MyEntity> findAll(Pageable pageable);
}  
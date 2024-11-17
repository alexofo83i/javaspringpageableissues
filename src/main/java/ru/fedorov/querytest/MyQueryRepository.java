package ru.fedorov.querytest;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MyQueryRepository extends PagingAndSortingRepository<MyEntity, Long>
                                           , CrudRepository<MyEntity, Long> {
        Page<MyEntity> findAll(Pageable pageable);
}  
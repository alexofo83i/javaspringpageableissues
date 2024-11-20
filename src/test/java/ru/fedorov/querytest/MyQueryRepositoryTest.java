package ru.fedorov.querytest;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;
import ru.fedorov.querytest.entity.MyEntity;
import ru.fedorov.querytest.repository.MyQueryRepository;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
@Slf4j
public class MyQueryRepositoryTest {

    @Autowired
    MyQueryRepository testQueryRepository;

    @Autowired
    ApplicationContext appContext;

    @ParameterizedTest
    @MethodSource("providePageSizePageNumber")
    void testFindAllPaged(int pageSize, int pageNumber){
        // log.info( appContext.get);

        Pageable pageable = Pageable.ofSize(pageSize);
        pageable = pageable.withPage(pageNumber);

        Page<MyEntity> page = testQueryRepository.findAll( pageable );
        assertNotEquals( page.getNumberOfElements(), 0);
    }

    public static Stream<Arguments> providePageSizePageNumber(){
        return Stream.of(
              arguments(20, 0)
            , arguments(20, 100)
            , arguments(100, 0)
            , arguments(100, 100)
        );
    }
}

package ru.fedorov.querytest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.extern.slf4j.Slf4j;
import ru.fedorov.querytest.entity.MyEntity;
import ru.fedorov.querytest.entity.MyEntityPaging;
import ru.fedorov.querytest.service.MyQueryService;

@SpringBootTest
@Slf4j
class MyQueryServiceTest {

    @Autowired
    MyQueryService testQueryService;
	

	@Test
	void testSimpleQuery() {
		String queryText = "select t.* from testentity t order by t.name limit 20 offset 10";
		List<MyEntity> result = testQueryService.getList(queryText, MyEntity.class);
		MyEntity obj = result.iterator().next();
		assertNotNull(obj);
		assertNotNull(obj.getName());
		MyEntity obj2 = result.iterator().next();
		assertEquals(obj, obj2);
	}

	@Test
	void testSimpleCountOverQuery() {
		String queryText = "select t.*, count(*) over() as total_cnt from testentity t order by t.name limit 20 offset 10";
		List<MyEntity> result = testQueryService.getList(queryText, MyEntity.class);
		MyEntity obj = result.iterator().next();
		assertNotNull(obj);
		assertNotNull(obj.getName());
		MyEntity obj2 = result.iterator().next();
		assertEquals(obj, obj2);
	}
	

	@Test
	void testSimpleCountQueryEntityPaging() {
		String queryText = "select t.id, t.name, count(*) over() as total_cnt from testentity t order by t.name limit 20 offset 10";
		List<MyEntityPaging> result = testQueryService.getListExtended(queryText, MyEntityPaging.class);
		MyEntityPaging obj = result.iterator().next();
		assertNotNull(obj);
		assertNotNull(obj.getName());
		assertNotNull(obj.getTotal_cnt());
	}

	@Test
	void testSimpleCountQueryEntityPagingContainer() {
		Pageable pageable = Pageable.ofSize(100).withPage(10);
		String queryText = "select t.id, t.name, count(*) over() as total_cnt from testentity t order by t.name";
		Page<MyEntity> result = testQueryService.<MyEntity>getListContainer(queryText, pageable, MyEntity.class);
		MyEntity obj = result.getContent().get(0);
		assertNotNull(obj);
		assertNotNull(obj.getName());
	}

	@Test
	void testGetPageByNumberUsingTransformer() {
		Pageable pageable = Pageable.ofSize(100).withPage(10);
		String queryTextTemplate = "select count(*) over() as total_cnt, t.id, t.name from testentity t order by t.name";
		Page<MyEntity> result = testQueryService.<MyEntity>getPageByNumberUsingTransformer(queryTextTemplate, pageable, ((Object[] x) -> {
			return new MyEntity( ((Integer) x[1] ).longValue(), (String) x[2]); 
		}));
		MyEntity obj = result.getContent().get(0);
		assertNotNull(obj);
		assertNotNull(obj.getName());
	}
}

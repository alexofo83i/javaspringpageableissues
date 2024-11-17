package ru.fedorov.querytest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

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
		assertNotNull(obj.name);
		MyEntity obj2 = result.iterator().next();
		assertEquals(obj, obj2);
	}

	@Test
	void testSimpleCountOverQuery() {
		String queryText = "select t.*, count(*) over() as total_cnt from testentity t order by t.name limit 20 offset 10";
		List<MyEntity> result = testQueryService.getList(queryText, MyEntity.class);
		MyEntity obj = result.iterator().next();
		assertNotNull(obj);
		assertNotNull(obj.name);
		MyEntity obj2 = result.iterator().next();
		assertEquals(obj, obj2);
	}
	

	@Test
	void testSimpleCountQueryEntityPaging() {
		String queryText = "select t.id, t.name, count(*) over() as total_cnt from testentity t order by t.name limit 20 offset 10";
		List<MyEntityPaging> result = testQueryService.getListExtended(queryText, MyEntityPaging.class);
		MyEntityPaging obj = result.iterator().next();
		assertNotNull(obj);
		assertNotNull(obj.name);
		assertNotNull(obj.total_cnt);
	}
}

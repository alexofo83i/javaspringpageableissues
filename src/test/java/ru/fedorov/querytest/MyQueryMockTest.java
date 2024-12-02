package ru.fedorov.querytest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import ru.fedorov.querytest.entity.MyEntity;

@SpringBootTest
public class MyQueryMockTest {

    // @Mock
    // EntityManager entityManager;

    @Autowired
    ObjectMapper objectMapper;

    @Test  @SuppressWarnings("unchecked")
	public void testGetPageByNumberUsingMapper(){
		Query query = mock(Query.class);
        List<Map<String, Object>> listStubs = new ArrayList<Map<String,Object>>(100);
        for( int i = 0; i < 100; i++){
            Map<String,Object> map = new HashMap<>();
            map.put("id", Long.valueOf(i));
            map.put("name", UUID.randomUUID().toString());
            map.put("total_cnt", Long.valueOf(1000000L));
            listStubs.add(map);
        }

        when(query.getResultList()).thenReturn(listStubs);

		List<Map<String, Object>> list = query.getResultList();

        Map<String,Object> resultMap0 = list.get(0);						
		long totalCnt =  (resultMap0 != null)? (long) resultMap0.get("total_cnt") : 0L;

        assertEquals(1000000, totalCnt);

		List<MyEntity> result = list.stream()
						.map(x-> objectMapper.convertValue(x, MyEntity.class))
						.toList();
        MyEntity entity = result.get(0);
        assertNotNull(entity);
        assertEquals(0, entity.getId());
	}

	@Test  @SuppressWarnings("unchecked")
	public void testGetPageByNumberUsingTransformer(){
		Query query = mock(Query.class);
        List<Object[]> listStubs = new ArrayList<Object[]>(100);
        for( int i = 0; i < 100; i++){
            Object[] tuple = new Object[]{Long.valueOf(1000000L), Long.valueOf(i), UUID.randomUUID().toString()};
            listStubs.add(tuple);
        }

        when(query.getResultList()).thenReturn(listStubs);

		List<Object[]> list = query.getResultList();
		List<MyEntity> result = list.stream()
						.map(((Object[] x) -> {
                            return new MyEntity( (Long) x[1], (String) x[2]); 
                        }))
						.toList();
        MyEntity entity = result.get(0);
        assertNotNull(entity);
        assertEquals(0, entity.getId());
		
        Object[] tuple = list.get(0);						
		long totalCnt =  (tuple != null)?  (Long) tuple[0] : 0L;
		assertEquals(1000000, totalCnt);
	}

}

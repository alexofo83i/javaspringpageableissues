package ru.fedorov.querytest.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@AllArgsConstructor
public class MyQueryService {
	private final EntityManager entityManager;
	private final ObjectMapper objectMapper;
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String queryText, Class<T> clazz){
		Query query = entityManager.createNativeQuery(queryText, clazz);
		List<T> result = (List<T>) query.getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getListExtended(String queryText, Class<T> clazz){
		Query query = entityManager.createNativeQuery(queryText, clazz);
		List<T> result = (List<T>)  query.getResultList();
		return result;
	}

	@SuppressWarnings("unchecked")
	public <T> Page<T> getListContainer(String queryText,  Pageable pageable, Class<T> clazz){
		Query query = entityManager.createNativeQuery(queryText,Map.class);
		List<Map<String, Object>> list = query.getResultList();
		List<T> result = list.stream()
						.map(x-> objectMapper.convertValue(x, clazz))
						.toList();
		Long totalCnt = (Long) list.get(0).get("total_cnt");
		return new PageImpl<>(result, pageable, totalCnt);
	}

	static final String QUERY_TEXT_COUNT_OVER_ORDERBY_NAME_LIMIT_OFFSET = "select count(*) over() as total_cnt, t.id, t.name from testentity t order by t.name limit $1 offset $2";
	@SuppressWarnings("unchecked")
	public <T> Page<T> getPageByNumberUsingMapper(Pageable pageable, Class<T> clazz){
		String queryText = QUERY_TEXT_COUNT_OVER_ORDERBY_NAME_LIMIT_OFFSET
							.replace("$1", String.valueOf( pageable.getPageSize()))
							.replace("$2", String.valueOf( pageable.getOffset()));
		Query query = entityManager.createNativeQuery(queryText,Map.class);
		List<Map<String, Object>> list = query.getResultList();
		List<T> result = list.stream()
						.map(x-> objectMapper.convertValue(x, clazz))
						.toList();
		Map<String,Object> resultMap0 = list.get(0);						
		long totalCnt =  (resultMap0 != null)? (long) resultMap0.get("total_cnt") : 0L;
		return new PageImpl<>(result, pageable, totalCnt);
	}

	@SuppressWarnings("unchecked")
	public <T> Page<T> getPageByNumberUsingTransformer(Pageable pageable, Function<Object[],T> transformer){
		String queryText = QUERY_TEXT_COUNT_OVER_ORDERBY_NAME_LIMIT_OFFSET
							.replace("$1", String.valueOf( pageable.getPageSize()))
							.replace("$2", String.valueOf( pageable.getOffset()));
		
		Query query = entityManager
					.createNativeQuery(queryText,Object[].class)
					// .setFirstResult((int)pageable.getOffset())
					// .setMaxResults(pageable.getPageSize())
					;
		List<Object[]> list = query.getResultList();
		List<T> result = list.stream()
						.map(transformer)
						.toList();
		Object[] tuple = list.get(0);						
		long totalCnt =  (tuple != null)? (long) tuple[0] : 0L;
		return new PageImpl<>(result, pageable, totalCnt);
	}
}

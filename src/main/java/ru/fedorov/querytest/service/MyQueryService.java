package ru.fedorov.querytest.service;

import java.util.List;
import java.util.Map;

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
		Long totalCnt = (Long) list.iterator().next().get("total_cnt");
		return new PageImpl<>(result, pageable, totalCnt);
	}
}

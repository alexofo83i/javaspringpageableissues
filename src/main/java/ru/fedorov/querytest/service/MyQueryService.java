package ru.fedorov.querytest.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MyQueryService {
	private final EntityManager entityManager;
	
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
}

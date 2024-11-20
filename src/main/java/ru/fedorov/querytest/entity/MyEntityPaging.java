package ru.fedorov.querytest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class MyEntityPaging  { //extends TestEntity{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id; 
	
	@Column(length = 10)
	String name;

	@Column
	Integer total_cnt;
}


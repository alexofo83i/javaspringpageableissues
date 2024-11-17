package ru.fedorov.querytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class MyQueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyQueryApplication.class, args);
	}

}

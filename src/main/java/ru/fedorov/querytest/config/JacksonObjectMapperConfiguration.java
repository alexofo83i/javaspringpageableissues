package ru.fedorov.querytest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
 
@Configuration
public class JacksonObjectMapperConfiguration {
    
        @Bean
        public ObjectMapper objectMapper() {
            var mapper = new ObjectMapper();
            SimpleFilterProvider simpleFilterProvider = new SimpleFilterProvider().setFailOnUnknownId(false);
            mapper.setFilterProvider(simpleFilterProvider);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper;
        }
    }

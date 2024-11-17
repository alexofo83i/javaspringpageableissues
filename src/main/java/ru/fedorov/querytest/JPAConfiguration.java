package ru.fedorov.querytest;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class JPAConfiguration {
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource datasource) {
        LocalContainerEntityManagerFactoryBean em  = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(datasource);
        // em.setPersistenceUnitName("mypersistenceunit");
        em.setPackagesToScan("ru.fedorov.querytest");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        // em.setJpaProperties(additionalProperties());

        return em;
    }

    // Properties additionalProperties() {
    //     Properties properties = new Properties();
    //     // properties.setProperty("spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action", "create");
    //     // properties.setProperty("spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target","create.sql");
    //     // properties.setProperty("spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-source","metadata");
    //     return properties;
    // }
}

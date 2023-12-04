package com.pricecomparison.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateUtil {


    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder sessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionFactoryBuilder.scanPackages("com.pricecomparison");
        sessionFactoryBuilder.addProperties(hibernateProperties());
        return sessionFactoryBuilder.buildSessionFactory();
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return hibernateProperties;
    }

    @Bean
    public static DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("pricecomparison_db");
        dataSource.setUsername("root");
        dataSource.setPassword("MohAmed_33454030");
        return dataSource;
    }
}

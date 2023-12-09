package com.pricecomparison.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * <h1>HibernateUtil class</h1>
 * <p>
 * is used to create a session factory for
 * hibernate and to create a data source for the database
 *
 * @see <a href="https://www.baeldung.com/hibernate-4-spring">https://www.baeldung.com/hibernate-4-spring</a>
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
@Configuration
public class HibernateUtil {

    /**
     * sessionFactory method is used to create a session factory for hibernate
     * @param dataSource is the data source for the database
     * @return the session factory
     */
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
        return hibernateProperties;
    }

    @Bean
    public static DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/pricecomparison_db");
        dataSource.setUsername("root");
        dataSource.setPassword("MohAmed_33454030");
        return dataSource;
    }
}
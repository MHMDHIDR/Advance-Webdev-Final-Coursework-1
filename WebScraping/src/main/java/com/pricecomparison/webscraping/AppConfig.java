package com.pricecomparison.webscraping;

import com.pricecomparison.util.HibernateUtil;
import com.pricecomparison.util.SaveModel;
import org.hibernate.SessionFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.pricecomparison")
public class AppConfig {

    @Bean
    public WebDriver webDriver() {
        return new ChromeDriver();
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder sessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionFactoryBuilder.scanPackages("com.pricecomparison");
        sessionFactoryBuilder.addProperties(hibernateProperties());
        return sessionFactoryBuilder.buildSessionFactory();
    }

    @Bean
    public DataSource dataSource() {
        return HibernateUtil.getDataSource();
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        return hibernateProperties;
    }

    @Bean
    public SaveModel saveModel() {
        SaveModel saveModel = new SaveModel();
        saveModel.setSessionFactory(sessionFactory(dataSource()));
        return new SaveModel();
    }

    @Bean
    public ScraperManager scraperManager() {
        ScraperManager scraperManager = new ScraperManager();
        ArrayList<Thread> scraperList = new ArrayList<>();;
        scraperList.add(amazonScraper());
        scraperList.add(eBayScraper());
        scraperList.add(argosScraper());
        scraperList.add(bestBuyScraper());
        scraperManager.setScraperList(scraperList);
        return scraperManager;
    }

    @Bean
    public AmazonScraper amazonScraper() {
        AmazonScraper amazonScraper = new AmazonScraper();
        amazonScraper.setSessionFactory(sessionFactory(dataSource()));
        amazonScraper.setDriver(webDriver());
        return amazonScraper;
    }

    @Bean
    public eBayScraper eBayScraper() {
        eBayScraper bestBuyScrape = new eBayScraper();
        bestBuyScrape.setSessionFactory(sessionFactory(dataSource()));
        bestBuyScrape.setDriver(webDriver());
        return bestBuyScrape;
    }

    @Bean
    public ArgosScraper argosScraper() {
        ArgosScraper argosScraper = new ArgosScraper();
        argosScraper.setSessionFactory(sessionFactory(dataSource()));
        argosScraper.setDriver(webDriver());
        return argosScraper;
    }

    @Bean
    public BestBuyScraper bestBuyScraper() {
        BestBuyScraper bestBuyScrape = new BestBuyScraper();
        bestBuyScrape.setSessionFactory(sessionFactory(dataSource()));
        bestBuyScrape.setDriver(webDriver());
        return bestBuyScrape;
    }


}

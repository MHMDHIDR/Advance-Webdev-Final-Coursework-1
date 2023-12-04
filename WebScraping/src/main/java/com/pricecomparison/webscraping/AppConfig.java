package com.pricecomparison.webscraping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ComponentScan(basePackages = "com.pricecomparison")
public class AppConfig {

//    @Bean
//    public WebDriver webDriver() {
//        return new ChromeDriver();
//    }

//    @Bean
//    public SessionFactory sessionFactory() {
//        try {
//            LocalSessionFactoryBuilder sessionFactoryBuilder = new LocalSessionFactoryBuilder(dataSource());
//            sessionFactoryBuilder.scanPackages("com.pricecomparison");
//            sessionFactoryBuilder.addProperties(hibernateProperties());
//            return sessionFactoryBuilder.buildSessionFactory();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    @Bean
//    public DataSource dataSource() {
//        return HibernateUtil.getDataSource();
//    }
//
//    @Bean
//    public Properties hibernateProperties() {
//        Properties hibernateProperties = new Properties();
//        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
//        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        return hibernateProperties;
//    }

//    @Bean
//    public SaveCase saveModel() {
//        SaveCase saveCase = new SaveCase();
//        saveCase.setSessionFactory(sessionFactory());
//        return new SaveCase();
//    }

    @Bean
    public ScraperManager scraperManager() {
        ScraperManager scraperManager = new ScraperManager();
        ArrayList<WebScrapper> scraperList = new ArrayList<>();;
//        scraperList.add(amazonScraper());
        scraperList.add(argosScraper());
//        scraperList.add(eBayScraper());
//        scraperList.add(bestBuyScraper());
        scraperManager.setScraperList(scraperList);
        return scraperManager;
    }

//    @Bean
//    public AmazonScraper amazonScraper() {
//        AmazonScraper amazonScraper = new AmazonScraper();
//        amazonScraper.setSaveModel(saveModel());
//        amazonScraper.setDriver(webDriver());
//        return amazonScraper;
//    }

    @Bean
    public ArgosScraper argosScraper() {
        ArgosScraper argosScraper = new ArgosScraper();
        argosScraper.setCaseDao(caseDao());
        return argosScraper;
    }

//    @Bean
//    public eBayScraper eBayScraper() {
//        eBayScraper bestBuyScrape = new eBayScraper();
//        bestBuyScrape.setSaveModel(saveModel());
//        bestBuyScrape.setDriver(webDriver());
//        return bestBuyScrape;
//    }
//
//    @Bean
//    public BestBuyScraper bestBuyScraper() {
//        BestBuyScraper bestBuyScrape = new BestBuyScraper();
//        bestBuyScrape.setSaveModel(saveModel());
//        bestBuyScrape.setDriver(webDriver());
//        return bestBuyScrape;
//    }

    @Bean
    public CaseDao caseDao() {
        CaseDao caseDao = new CaseDao();
        caseDao.init(); // Initialize the session factory
        return caseDao;
    }

}

package com.pricecomparison.webscraping;

import com.pricecomparison.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.pricecomparison")
public class AppConfig {

    @Bean
    public WebDriver webDriver() {
        return new ChromeDriver();
    }

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        return new HibernateUtil().sessionFactory(dataSource);
    }

    @Bean
    public AmazonScraper amazonScraper(SessionFactory sessionFactory) {
        return new AmazonScraper(webDriver(), sessionFactory);
    }

    @Bean
    public eBayScraper ebayScraper(SessionFactory sessionFactory) {
        return new eBayScraper(sessionFactory);
    }

    @Bean
    public BestBuyScraper bestBuyScraper(SessionFactory sessionFactory) {
        return new BestBuyScraper(sessionFactory);
    }

    @Bean
    public ArgosScraper argosScraper(SessionFactory sessionFactory) {
        return new ArgosScraper(sessionFactory);
    }

    @Bean
    public GumtreeScraper gumtreeScraper(SessionFactory sessionFactory) {
        return new GumtreeScraper(webDriver(), sessionFactory);
    }
}

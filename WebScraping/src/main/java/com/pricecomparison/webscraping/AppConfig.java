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

//    @Bean
//    public AmazonScraper amazonScraper() {
//        return new AmazonScraper(webDriver(), sessionFactory(null));
//    }

//    @Bean
//    public eBayScraper ebayScraper() {
//        return new eBayScraper(sessionFactory(null));
//    }

    @Bean
    public BestBuyScraper bestBuyScraper() {
        return new BestBuyScraper(sessionFactory(null));
    }

//    @Bean
//    public ArgosScraper argosScraper() {
//        return new ArgosScraper(sessionFactory(null));
//    }

//    @Bean
//    public GumtreeScraper gumtreeScraper() {
//        return new GumtreeScraper(sessionFactory(null));
//    }
}

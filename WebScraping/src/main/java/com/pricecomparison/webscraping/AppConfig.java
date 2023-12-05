package com.pricecomparison.webscraping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@ComponentScan(basePackages = "com.pricecomparison")
public class AppConfig {
    @Bean
    public ScraperManager scraperManager() {
        ScraperManager scraperManager = new ScraperManager();
        ArrayList<WebScrapper> scraperList = new ArrayList<>();;
        scraperList.add(amazonScraper());
        scraperList.add(argosScraper());
        scraperList.add(eBayScraper());
        scraperList.add(bestBuyScraper());
        scraperManager.setScraperList(scraperList);
        return scraperManager;
    }

    @Bean
    public AmazonScraper amazonScraper() {
        AmazonScraper amazonScraper = new AmazonScraper();
        amazonScraper.setCaseDao(caseDao());
        return amazonScraper;
    }

    @Bean
    public ArgosScraper argosScraper() {
        ArgosScraper argosScraper = new ArgosScraper();
        argosScraper.setCaseDao(caseDao());
        return argosScraper;
    }

    @Bean
    public eBayScraper eBayScraper() {
        eBayScraper bestBuyScrape = new eBayScraper();
        bestBuyScrape.setCaseDao(caseDao());
        return bestBuyScrape;
    }

    @Bean
    public BestBuyScraper bestBuyScraper() {
        BestBuyScraper bestBuyScrape = new BestBuyScraper();
        bestBuyScrape.setCaseDao(caseDao());
        return bestBuyScrape;
    }

    @Bean
    public CaseDao caseDao() {
        CaseDao caseDao = new CaseDao();
        // Initialize the session factory
        caseDao.init();
        return caseDao;
    }

}

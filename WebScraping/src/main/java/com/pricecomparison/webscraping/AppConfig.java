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
        //scraperList.add(amazonScraper());
        //scraperList.add(argosScraper());
        //scraperList.add(eBayScraper());
        scraperList.add(bestBuyScraper());
        //scraperList.add(backmarketScraper());
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
        eBayScraper bestBuyScraper = new eBayScraper();
        bestBuyScraper.setCaseDao(caseDao());
        return bestBuyScraper;
    }

    @Bean
    public BestBuyScraper bestBuyScraper() {
        BestBuyScraper bestBuyScraper = new BestBuyScraper();
        bestBuyScraper.setCaseDao(caseDao());
        return bestBuyScraper;
    }

    @Bean
    public BackmarketScraper backmarketScraper() {
        BackmarketScraper backmarketScraper = new BackmarketScraper();
        backmarketScraper.setCaseDao(caseDao());
        return backmarketScraper;
    }

    @Bean
    public CaseDao caseDao() {
        CaseDao caseDao = new CaseDao();
        // Initialize the session factory
        caseDao.init();
        return caseDao;
    }

}

package com.pricecomparison.webscraping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * AppConfig class is used to create beans for the application
 * It has a bean for the ScraperManager and a bean for each WebScrapper.
 * <p>
 *     <b>Note:</b> This class is used by the ScraperManager class.
 * </p>
 *
 * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
@Configuration
@ComponentScan(basePackages = "com.pricecomparison")
public class AppConfig {
    @Bean
    /*
    * Method is used to create a ScraperManager bean that
    * will be used to manage the scrapers and scrape the websites.
    */
    public ScraperManager scraperManager() {
        ScraperManager scraperManager = new ScraperManager();
        ArrayList<WebScrapper> scraperList = new ArrayList<>();;
        scraperList.add(amazonScraper());
        scraperList.add(argosScraper());
        scraperList.add(eBayScraper());
        scraperList.add(bestBuyScraper());
        scraperList.add(backmarketScraper());
        scraperManager.setScraperList(scraperList);
        return scraperManager;
    }

    @Bean
    /*
     * AmazonScraper bean will be used to scrape the Amazon website.
     * Also, the CaseDao bean is injected into the AmazonScraper bean
     */
    public AmazonScraper amazonScraper() {
        AmazonScraper amazonScraper = new AmazonScraper();
        amazonScraper.setCaseDao(caseDao());
        return amazonScraper;
    }

    @Bean
    /*
     * ArgosScraper bean will be used to scrape the Argos website.
     * Also, the CaseDao bean is injected into the ArgosScraper bean
     */
    public ArgosScraper argosScraper() {
        ArgosScraper argosScraper = new ArgosScraper();
        argosScraper.setCaseDao(caseDao());
        return argosScraper;
    }

    @Bean
    /*
     * eBayScraper bean will be used to scrape the eBay website.
     * Also, the CaseDao bean is injected into the eBayScraper bean
     */
    public eBayScraper eBayScraper() {
        eBayScraper bestBuyScraper = new eBayScraper();
        bestBuyScraper.setCaseDao(caseDao());
        return bestBuyScraper;
    }

    @Bean
    /* BestBuyScraper bean will be used to scrape the BestBuy website.
     * Also, the CaseDao bean is injected into the BestBuyScraper bean
     */
    public BestBuyScraper bestBuyScraper() {
        BestBuyScraper bestBuyScraper = new BestBuyScraper();
        bestBuyScraper.setCaseDao(caseDao());
        return bestBuyScraper;
    }

    @Bean
    /* BackmarketScraper bean will be used to scrape the Backmarket website.
      Also, the CaseDao bean is injected into the BackmarketScraper bean
     */
    public BackmarketScraper backmarketScraper() {
        BackmarketScraper backmarketScraper = new BackmarketScraper();
        backmarketScraper.setCaseDao(caseDao());
        return backmarketScraper;
    }

    /**
     * Method is used to create a CaseDao bean that
     * will be used to save the scraped data to the database.
     * it also initializes the session factory.
     */
    @Bean
    public CaseDao caseDao() {
        CaseDao caseDao = new CaseDao();
        // Initialize the session factory
        caseDao.init();
        return caseDao;
    }

}

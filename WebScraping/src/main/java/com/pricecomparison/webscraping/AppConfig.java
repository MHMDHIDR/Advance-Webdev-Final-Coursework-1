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
 *     and it used to initialize the ScraperManager bean and the WebScrapper beans.
 * </p>
 *
 * <b> Example: AppConfig using ScraperManager and AmazonScraper beans</b>
 * <pre>
 *       {@code
 *        @Configuration
 *        @ComponentScan(basePackages = "com.pricecomparison")
 *        public class AppConfig {
 *            @Bean
 *            public ScraperManager scraperManager() {
 *                 ScraperManager scraperManager = new ScraperManager();
 *                 ArrayList<WebScrapper> scraperList = new ArrayList<>();;
 *                 scraperList.add(amazonScraper());
 *                 scraperList.add(argosScraper());
 *                 scraperList.add(eBayScraper());
 *                 scraperList.add(bestBuyScraper());
 *                 scraperList.add(backmarketScraper());
 *                 scraperManager.setScraperList(scraperList);
 *                 return scraperManager;
 *            }
 *            @Bean
 *            public AmazonScraper amazonScraper() {
 *                 AmazonScraper amazonScraper = new AmazonScraper();
 *                 amazonScraper.setCaseDao(caseDao());
 *                 return amazonScraper;
 *            }
 *        }
 *     }
 * </pre>
 *
 * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
@Configuration
@ComponentScan(basePackages = "com.pricecomparison")
public class AppConfig {
    /**
     * AppConfig constructor
     * It is used to create an AppConfig object
     */
    public AppConfig() {}

    /**
     * Method is used to create a ScraperManager bean that
     * will be used to manage the scrapers and scrape the websites.
     * it also initializes the WebScrapper beans.
     * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
     * @return ScraperManager bean
     */
    @Bean
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

    /**
     * AmazonScraper bean will be used to scrape the Amazon website.
     * Also, the CaseDao bean is injected into the AmazonScraper bean
     * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
     * @return AmazonScraper bean
     */
    @Bean
    public AmazonScraper amazonScraper() {
        AmazonScraper amazonScraper = new AmazonScraper();
        amazonScraper.setCaseDao(caseDao());
        return amazonScraper;
    }

    /**
     * ArgosScraper bean will be used to scrape the Argos website.
     * Also, the CaseDao bean is injected into the ArgosScraper bean
     * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
     * @return ArgosScraper bean
     */
    @Bean
    public ArgosScraper argosScraper() {
        ArgosScraper argosScraper = new ArgosScraper();
        argosScraper.setCaseDao(caseDao());
        return argosScraper;
    }

    /**
     * eBayScraper bean will be used to scrape the eBay website.
     * Also, the CaseDao bean is injected into the eBayScraper bean
     * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
     * @return eBayScraper bean
     */
    @Bean
    public eBayScraper eBayScraper() {
        eBayScraper bestBuyScraper = new eBayScraper();
        bestBuyScraper.setCaseDao(caseDao());
        return bestBuyScraper;
    }

    /**
     * BestBuyScraper bean will be used to scrape the BestBuy website.
     * Also, the CaseDao bean is injected into the BestBuyScraper bean
     * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
     * @return BestBuyScraper bean
     */
    @Bean
    public BestBuyScraper bestBuyScraper() {
        BestBuyScraper bestBuyScraper = new BestBuyScraper();
        bestBuyScraper.setCaseDao(caseDao());
        return bestBuyScraper;
    }

    /**
     * BackmarketScraper bean will be used to scrape the Backmarket website.
     * Also, the CaseDao bean is injected into the BackmarketScraper bean
     * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
     * @return BackmarketScraper bean
     */
    @Bean
    public BackmarketScraper backmarketScraper() {
        BackmarketScraper backmarketScraper = new BackmarketScraper();
        backmarketScraper.setCaseDao(caseDao());
        return backmarketScraper;
    }

    /**
     * Method is used to create a CaseDao bean that
     * will be used to save the scraped data to the database.
     * it also initializes the session factory.
     * @see <a href="https://www.javatpoint.com/spring-bean">Spring Bean</a>
     * @return CaseDao bean
     */
    @Bean
    public CaseDao caseDao() {
        CaseDao caseDao = new CaseDao();
        // Initialize the session factory
        caseDao.init();
        return caseDao;
    }
}

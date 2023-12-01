package com.pricecomparison.webscraping;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App {
    public static void main(String[] args) {
        // Create the Spring context
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve the scrapers from the context
        //AmazonScraper amazonScraperThread = context.getBean(AmazonScraper.class);
        //eBayScraper ebayScraperThread = context.getBean(eBayScraper.class);
        //BestBuyScraper BestBuyScraperThread = context.getBean(BestBuyScraper.class);
        ArgosScraper ArgosScraperThread = context.getBean(ArgosScraper.class);


        // Start the scraper threads
        //amazonScraperThread.start();
        //ebayScraperThread.start();
        //BestBuyScraperThread.start();
        ArgosScraperThread.start();

        // Wait for the threads to finish
        try {
            //amazonScraperThread.join();
            //ebayScraperThread.join();
            //BestBuyScraperThread.join();
            ArgosScraperThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted => " + e.getMessage());
        }
    }
}

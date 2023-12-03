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
        //ArgosScraper ArgosScraperThread = context.getBean(ArgosScraper.class);
        BestBuyScraper BestBuyScraperThread = context.getBean(BestBuyScraper.class);


        // Start the scraper threads
        //amazonScraperThread.start();
        //ebayScraperThread.start();
        //ArgosScraperThread.start();
        BestBuyScraperThread.start();

        // Wait for the threads to finish
        try {
            //amazonScraperThread.join();
            //ebayScraperThread.join();
            //ArgosScraperThread.join();
            BestBuyScraperThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted => " + e.getMessage());
        }
    }
}

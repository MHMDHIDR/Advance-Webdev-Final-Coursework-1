package com.pricecomparison.webscraping;

/**
 * Web Scraping threads
 * Created by: Mohammed Haydar
 * version: 0.0.1
 * date: 28.10.2023
 */
public class App {
    public static void main(String[] args) {
        // Create and start the AmazonScraper thread
        //AmazonScraper amazonScraperThread = new AmazonScraper();
        //amazonScraperThread.start();

        // Create and start the eBayScraper thread
        //eBayScraper ebayScraperThread = new eBayScraper();
        //ebayScraperThread.start();

        // Create and start the Walmart thread
        //WalmartScraper walmartScraperThread = new WalmartScraper();
        //walmartScraperThread.start();

        // Create and start the BestBuyScraper thread
        //BestBuyScraper BestBuyScraperThread = new BestBuyScraper();
        //BestBuyScraperThread.start();

        // Create and start the TargetScraper thread
        ArgosScraper ArgosScraperThread = new ArgosScraper();
        ArgosScraperThread.start();

        try {
            //amazonScraperThread.join();
            //ebayScraperThread.join();
            //walmartScraperThread.join();
            //BestBuyScraperThread.join();
            ArgosScraperThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted.");
        }
    }
}

package com.pricecomparison.webscraping;

import static java.lang.Thread.sleep;

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
        eBayScraper ebayScraperThread = new eBayScraper();
        ebayScraperThread.start();

        // Create and start the BestBuyScraper thread
        //BestBuyScraper BestBuyScraperThread = new BestBuyScraper();
        //BestBuyScraperThread.start();

        // Create and start the TargetScraper thread
        //ArgosScraper ArgosScraperThread = new ArgosScraper();
        //ArgosScraperThread.start();

        // Create and start the TargetScraper thread
        //GumtreeScraper GumtreeScraperThread = new GumtreeScraper();
        //GumtreeScraperThread.start();

        try {
            //amazonScraperThread.join();
            //sleep(1000);

            ebayScraperThread.join();
            //sleep(1000);

            //BestBuyScraperThread.join();
            //sleep(1000);

            //ArgosScraperThread.join();
            //sleep(1000);

            //GumtreeScraperThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted => " + e.getMessage());
        }
    }
}

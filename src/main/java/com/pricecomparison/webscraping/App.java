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
        AmazonScraper amazonScraperThread = new AmazonScraper();
        amazonScraperThread.start();

        try {
            // Wait for the AmazonScraper thread to finish
            amazonScraperThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted.");
        }
    }
}

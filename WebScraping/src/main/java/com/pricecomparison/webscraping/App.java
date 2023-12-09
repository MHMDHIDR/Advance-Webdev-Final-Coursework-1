package com.pricecomparison.webscraping;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App {
    /**
     * <h1>Main method to start scrapper manager</h1>
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Create the Spring context
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            // Retrieve the scrapers from the context
            ScraperManager scraperManager = (ScraperManager) context.getBean("scraperManager");
            // Start scraping
            scraperManager.startScraping();
        }
    }
}

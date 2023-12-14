package com.pricecomparison.webscraping;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * <h2>App class used to call main method to start the scrapper manager</h2>
 * @see App#main(String[])
 * @version 1.0
 */
public class App {
    /**
     * This is the constructor of the App class.
     * It creates a new App object.
     * and starts the Spring context.
     *
     * @see App#App()
     */
    public App() {}

    /**
     * Main method to start scrapper manager
     * <p>
     *     This method starts the Spring context and retrieves the scrapers from the context.
     *     Then it starts scraping.
     *     Each scraper is run in a separate thread.
     * </p>
     *
     * @param args the command line arguments
     *
     * @see App#main(String[])
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

package com.pricecomparison.webscraping;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App {
    public static void main(String[] args) {
        // Create the Spring context
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve the scrapers from the context
        ScraperManager scraperManager = (ScraperManager) context.getBean("scraperManager");
        scraperManager.startScraping();

        // Retrieve the scrapers from the context
        //AmazonScraper amazonScraper = context.getBean(AmazonScraper.class);
        //amazonScraper.start();

//        try {
//            amazonScraper.join();
//        } catch (InterruptedException e) {
//            System.out.println("Thread was interrupted => " + e.getMessage());
//        }

    }
}

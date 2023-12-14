package com.pricecomparison.webscraping;

import java.util.ArrayList;

/**
 * ScraperManager class is used to manage the list of WebScrapper objects.
 * It is used to start the scraping process.
 */
public class ScraperManager {
    ArrayList<WebScrapper> scraperList;

    /**
     * Constructor for ScraperManager class.
     * (No specific logic in this constructor.)
     */
    public ScraperManager() {}

    /**
     * Starts the scraping process by invoking the start method on each WebScrapper object.
     */
    public void startScraping() {
        for(WebScrapper scraper : scraperList) {
            scraper.start();
        }
    }


    /**
     * getScraperList Getter to get the list of WebScrapper objects.
     *
     * @return scraperList the list of WebScrapper objects
     */
    public ArrayList<WebScrapper> getScraperList() {
        return scraperList;
    }

    /**
     * Setter to set the list of WebScrapper objects.
     * @param scraperList list of WebScrapper objects
     */
    public void setScraperList(ArrayList<WebScrapper> scraperList) {
        this.scraperList = scraperList;
    }
}

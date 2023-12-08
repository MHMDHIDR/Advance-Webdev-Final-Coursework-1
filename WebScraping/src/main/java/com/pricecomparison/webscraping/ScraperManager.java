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
     */
    public void startScraping(){
        for(WebScrapper scraper : scraperList) {
            scraper.start();
        }
    }

    /**
     * Getter to get the list of WebScrapper objects.
     */
    public ArrayList<WebScrapper> getScraperList() {
        return scraperList;
    }

    /**
     * Setter to set the list of WebScrapper objects.
     */
    public void setScraperList(ArrayList<WebScrapper> scraperList) {
        this.scraperList = scraperList;
    }
}

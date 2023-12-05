package com.pricecomparison.webscraping;

import java.util.ArrayList;

public class ScraperManager {
    ArrayList<WebScrapper> scraperList;

    public void startScraping(){
        for(WebScrapper scraper : scraperList) {
            scraper.start();
        }
    }

    public ArrayList<WebScrapper> getScraperList() {
        return scraperList;
    }

    public void setScraperList(ArrayList<WebScrapper> scraperList) {
        this.scraperList = scraperList;
    }
}

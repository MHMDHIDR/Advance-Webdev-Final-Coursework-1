package com.pricecomparison.webscraping;

import java.util.ArrayList;

public class ScraperManager {
    ArrayList<Thread> scraperList;

    public void startScraping(){
        for(Thread scraper : scraperList){
            scraper.start();
        }
    }

    public ArrayList<Thread> getScraperList() {
        return scraperList;
    }

    public void setScraperList(ArrayList<Thread> scraperList) {
        this.scraperList = scraperList;
    }
}

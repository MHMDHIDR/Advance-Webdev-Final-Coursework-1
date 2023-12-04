package com.pricecomparison.webscraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebScrapper extends Thread{
    CaseDao caseDao;

    public CaseDao getCaseDao() {
        return caseDao;
    }

    public void setCaseDao(CaseDao caseDao) {
        this.caseDao = caseDao;
    }

    public WebDriver getDriver() {
        return new ChromeDriver();
    }
}

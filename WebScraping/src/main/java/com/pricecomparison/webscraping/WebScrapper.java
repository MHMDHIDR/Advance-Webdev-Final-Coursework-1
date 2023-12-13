package com.pricecomparison.webscraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

/**
 * This class includes getters and setters for the CaseDao, WebDriver and Cookies.
 *
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class WebScrapper extends Thread {
    CaseDao caseDao;
    WebDriver driver;

    public CaseDao getCaseDao() {
        return caseDao;
    }

    public void setCaseDao(CaseDao caseDao) {
        this.caseDao = caseDao;
    }

    public WebDriver getDriver() {
        if (driver == null) {
            driver = new ChromeDriver();
            //Set the page load timeout
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        }
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

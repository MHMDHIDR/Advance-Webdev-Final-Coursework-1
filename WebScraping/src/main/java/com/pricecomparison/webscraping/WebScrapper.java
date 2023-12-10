package com.pricecomparison.webscraping;

import com.pricecomparison.util.Cookies;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * This class includes getters and setters for the CaseDao, WebDriver and Cookies.
 *
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class WebScrapper extends Thread {
    CaseDao caseDao;
    Cookies cookies;
    WebDriver driver;
    JavascriptExecutor jsExecutor;

    public CaseDao getCaseDao() {
        return caseDao;
    }

    public void setCaseDao(CaseDao caseDao) {
        this.caseDao = caseDao;
    }

    public WebDriver getDriver() {
        if (driver == null) {
            driver = new ChromeDriver();
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

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

    /**
     * This is the constructor of the WebScrapper class.
     */
    public WebScrapper() {}

    /**
     * This method is used to get the CaseDao.
     * @return CaseDao
     * @see CaseDao
     */
    public CaseDao getCaseDao() {
        return caseDao;
    }

    /**
     * This method is used to set the CaseDao.
     * @param caseDao CaseDao object
     * @see CaseDao
     */
    public void setCaseDao(CaseDao caseDao) {
        this.caseDao = caseDao;
    }

    /**
     * This method is used to get the WebDriver.
     * If the WebDriver is null, it will create a new WebDriver.
     * It will set the page load timeout to 20 seconds.
     * @return WebDriver
     * @see WebDriver
     */
    public WebDriver getDriver() {
        if (driver == null) {
            driver = new ChromeDriver();
            //Set the page load timeout
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        }
        return driver;
    }

    /**
     * This method is used to set the WebDriver.
     * @param driver WebDriver
     * @see WebDriver
     */
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * This method is used to quit the WebDriver.
     * If the WebDriver is not null, it will quit the WebDriver.
     *
     * @see WebDriver
     */
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

package com.pricecomparison.test.webscraping;

import com.pricecomparison.webscraping.ArgosScraper;
import com.pricecomparison.webscraping.CaseDao;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the ArgosScraper class.
 *
 * @see ArgosScraper
 * @see ArgosScraper#start()
 *
 * @author Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since 2023-12-10
 */
public class ArgosScraperTest {
    @Test
    public void testArgosScraper() {
        WebDriver driver = new ChromeDriver();

        // Create a mock for CaseDao
        CaseDao caseDao = new CaseDao();

        // Create an instance of ArgosScraper using the real WebDriver
        ArgosScraper argosScraper = new ArgosScraper();
        argosScraper.setDriver(driver);

        // Set other dependencies if needed, e.g., CaseDao
        argosScraper.setCaseDao(caseDao);

        // Proceed with your test as usual
        argosScraper.start();

        // Assert the page source is not null
        String pageSource = driver.getPageSource();
        assertNotNull(pageSource);

        try {
            // Adding the delay making JavaScript to the scroll through the page before quitting the driver
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            argosScraper.quitDriver();
        }
    }
}
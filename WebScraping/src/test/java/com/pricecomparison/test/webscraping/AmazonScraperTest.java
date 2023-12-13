package com.pricecomparison.test.webscraping;

import com.pricecomparison.webscraping.AmazonScraper;
import com.pricecomparison.webscraping.CaseDao;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * This class tests the AmazonScraper class.
 *
 * @see AmazonScraper
 * @see AmazonScraper#start()
 *
 * @author Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since 2023-12-10
 */
public class AmazonScraperTest {
    @Test
    public void testAmazonScraper() {
        // Create a mock for CaseDao
        CaseDao caseDao = mock(CaseDao.class);

        // Create an instance of AmazonScraper using the real WebDriver
        AmazonScraper amazonScraper = new AmazonScraper();

        // Setting the dependencies (injecting the mock caseDao)
        amazonScraper.setCaseDao(caseDao);

        // Proceed with your test as usual
        amazonScraper.start();

        //Wait for thread to finish
        try {
            amazonScraper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(caseDao, atLeast(10)).filtered(anyString());

        try {
            // Adding the delay making JavaScript to the scroll through the page before quitting the driver
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            amazonScraper.quitDriver();
        }
    }
}
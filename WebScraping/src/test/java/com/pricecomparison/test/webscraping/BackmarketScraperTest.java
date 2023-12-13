package com.pricecomparison.test.webscraping;

import com.pricecomparison.webscraping.BackmarketScraper;
import com.pricecomparison.webscraping.CaseDao;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * This class tests the Backmarket Scraper class.
 * It uses a mock CaseDao to test the scraper.
 * The test is done by verifying that the CaseDao is called at least 10 times.
 *
 * @see BackmarketScraper
 * @see BackmarketScraper#start()
 *
 * @author Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since 2023-12-10
 */
public class BackmarketScraperTest {
    @Test
    public void testBackmarketScraper() {
        // Create a mock for CaseDao
        CaseDao caseDao = mock(CaseDao.class);

        // Create an instance of BackmarketScraper using the real WebDriver
        BackmarketScraper backmarketScraper = new BackmarketScraper();

        // Setting the dependencies (injecting the mock caseDao)
        backmarketScraper.setCaseDao(caseDao);

        // Proceed with your test as usual
        backmarketScraper.start();

        //Wait for thread to finish
        try {
            backmarketScraper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(caseDao, atLeast(10)).filtered(anyString());

        try {
            // Adding the delay making JavaScript to the scroll through the page
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            backmarketScraper.quitDriver();
        }
    }
}
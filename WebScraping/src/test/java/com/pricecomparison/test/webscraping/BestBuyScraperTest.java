package com.pricecomparison.test.webscraping;

import com.pricecomparison.webscraping.BestBuyScraper;
import com.pricecomparison.webscraping.CaseDao;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

/**
 * This class tests the BestBuyScraper class.
 *
 * @see BestBuyScraper
 * @see BestBuyScraper#start()
 *
 * @author Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since 2023-12-10
 */
public class BestBuyScraperTest {
    @Test
    public void testBestBuyScraper() {
        // Create a mock for CaseDao
        CaseDao caseDao = mock(CaseDao.class);

        // Create an instance of BestBuyScraper using the real WebDriver
        BestBuyScraper bestBuyScraper = new BestBuyScraper();

        // Setting the dependencies (injecting the mock caseDao)
        bestBuyScraper.setCaseDao(caseDao);

        // Proceed with your test as usual
        bestBuyScraper.start();

        //Wait for thread to finish
        try {
            bestBuyScraper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the printData() and saveCase() methods are called at least 10 times --> or I can also try calling them atLeastOnce()
        verify(caseDao, atLeast(10)).printData( anyString(),anyString(),anyString(),anyString(),anyString(),anyString());

        try {
            // Adding the delay
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            bestBuyScraper.quitDriver();
        }
    }
}
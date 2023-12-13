package com.pricecomparison.test.webscraping;

import com.pricecomparison.webscraping.eBayScraper;
import com.pricecomparison.webscraping.CaseDao;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;

import static org.mockito.Mockito.*;

/**
 * This class tests the eBayScraper class.
 *
 * @see eBayScraper
 * @see eBayScraper#start()
 *
 * @author Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since 2023-12-10
 */
public class eBayScraperTest {
    @Test
    public void testeBayScraper() {
        // Create a mock for CaseDao
        CaseDao caseDao = mock(CaseDao.class);

        // Create an instance of eBayScraper using the real WebDriver
        eBayScraper eBayScraper = new eBayScraper();

        // Setting the dependencies (injecting the mock caseDao)
        eBayScraper.setCaseDao(caseDao);

        // Proceed with your test as usual
        eBayScraper.start();

        //Wait for thread to finish
        try {
            eBayScraper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the filtered and printData methods were called at least 10 times
        verify(caseDao, atLeast(10)).filtered(anyString());
        verify(caseDao, atLeast(10)).printData(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

        try {
            // Adding the delay making JavaScript to the scroll through the page before quitting the driver
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            eBayScraper.quitDriver();
        }
    }
}
package com.pricecomparison.test.webscraping;

import com.pricecomparison.webscraping.ArgosScraper;
import com.pricecomparison.webscraping.CaseDao;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

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
        // Create a mock for CaseDao
        CaseDao caseDao = mock(CaseDao.class);

        // Create an instance of ArgosScraper using the real WebDriver
        ArgosScraper argosScraper = new ArgosScraper();

        // Setting the dependencies (injecting the mock caseDao)
        argosScraper.setCaseDao(caseDao);

        // Proceed with your test as usual
        argosScraper.start();

        //Wait for thread to finish
        try {
            argosScraper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        verify(caseDao, atLeast(10)).filtered(anyString());
        verify(caseDao, atLeast(10)).printData(anyString(), anyString(), anyString(), anyString(), anyString(), anyString());

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
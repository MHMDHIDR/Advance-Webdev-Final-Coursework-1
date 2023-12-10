package com.pricecomparison.test.webscraping;

import com.pricecomparison.webscraping.ArgosScraper;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This class tests the ArgosScraper class.
 *
 * @see ArgosScraper
 * @see ArgosScraper#start()
 *
 *
 * @author Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since 2023-12-10
 */
public class ArgosScraperTest {
    @Test
    void testArgosScraper() {
        // Mock the WebDriver
        WebDriver mockDriver = mock(WebDriver.class);

        // Create an instance of ArgosScraper
        ArgosScraper argosScraper = new ArgosScraper();
        argosScraper.setDriver(mockDriver);

        // Mock the behavior for the get method of the WebDriver
        when(mockDriver.getPageSource()).thenReturn("<html>YourMockedPage</html>");

        // Call the run method on the argosScraper
        argosScraper.start();

        // Verify that the WebDriver was called with the correct URL
        verify(mockDriver).get("https://www.argos.co.uk/search/iphone-case/opt/page:1");

        // Clean up resources
        argosScraper.quitDriver();
    }
}

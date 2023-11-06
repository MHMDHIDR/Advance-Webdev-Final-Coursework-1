package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.util.CurrencyConverter;
import com.pricecomparison.util.HibernateUtil;
import org.hibernate.Session;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.List;

public class WalmartScraper extends Thread {
    @Override
    public void run() {
        // Set up your Java project and configure Selenium
        System.setProperty("webdriver.chrome.driver", "/Users/mhmdhidr/chromedriver/chromedriver");
        WebDriver driver = new ChromeDriver();

        // Initialize Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Define the base URL
        String baseUrl = "https://www.walmart.com/search?q=iPhone+Case";

        try {
            driver.get(baseUrl);

            // Wait for the product elements to become available
            List<WebElement> productElements = driver.findElements(By.cssSelector("[data-item-id]"));

            for (WebElement productElement : productElements) {
                String productName = productElement.findElement(By.cssSelector("[data-automation-id='product-title']")).getText();
                String productPriceUSD = productElement.findElement(By.cssSelector("[data-testid='list-view'] span.w_iUH7")).getText();
                String productPrice = CurrencyConverter.convertToGBP(productPriceUSD);
                String productLink = productElement.findElement(By.cssSelector("a[link-identifier]")).getAttribute("href");
                String imageElement = productElement.findElement(By.cssSelector("img[data-testid='productTileImage']")).getAttribute("src");

                // Check if any essential data is missing
                if (productName.isEmpty() || productPrice.isEmpty() || productLink.isEmpty() || imageElement.isEmpty()) {
                    continue; // Skip this product
                }

                // Create a PhoneCase object and set its properties
                PhoneCase phoneCase = new PhoneCase();
                phoneCase.setName(productName);
                phoneCase.setPrice(productPrice);
                phoneCase.setDescription(productName);
                phoneCase.setWebsiteLink(productLink);
                phoneCase.setProductImageUrl(imageElement);

                // Save the PhoneCase object to the database
                session.save(phoneCase);
            }
        } catch (org.openqa.selenium.TimeoutException e) {
            System.err.println("Element not found even after waiting. Skipping this product. " + e.getMessage());
        } finally {
            // Commit the transaction and close the Hibernate session
            session.getTransaction().commit();
            session.close();

            // Close the browser
            //driver.quit();
            System.out.println("✔ WalmartScraper finished scraping.");
        }
    }
}
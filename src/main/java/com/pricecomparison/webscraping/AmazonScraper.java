package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.util.HibernateUtil;
import org.hibernate.Session;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AmazonScraper extends Thread {
    @Override
    public void run() {
        // Set up your Java project and configure Selenium
        System.setProperty("webdriver.chrome.driver", "/Users/mhmdhidr/chromedriver/chromedriver");

        // Create a new instance of the Chrome driver
        WebDriver driver = new ChromeDriver();

        // Initialize Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        // Initialize counters for tracking scraped and skipped items
        int scrapedItems = 0;
        int skippedItems = 0;

        boolean cookiesAccepted = false;

        for (int pageNumber = 1; pageNumber <= 10; pageNumber++) {
            String url = "https://www.amazon.co.uk/s?k=iPhone+case&page=" + pageNumber;
            driver.get(url);

            if (!cookiesAccepted) {
                try {
                    WebElement cookiesButton = driver.findElement(By.id("sp-cc-accept"));
                    cookiesButton.click();
                    System.out.println("Accept Cookies button clicked.");
                    cookiesAccepted = true;
                } catch (NoSuchElementException e) {
                    System.err.println("Accept Cookies button not found. Continuing without clicking it.");
                }
            }

            // Find and process each product on the page
            for (WebElement product : driver.findElements(By.cssSelector("div[data-component-type='s-search-result']"))) {
                try {
                    String productName = product.findElement(By.cssSelector("span.a-text-normal")).getText();
                    String productLink = product.findElement(By.cssSelector("a.a-link-normal.s-no-outline")).getAttribute("href");
                    String productDescription = product.findElement(By.cssSelector("span.a-text-normal")).getText();
                    String productImageSRC = product.findElement(By.cssSelector("img.s-image")).getAttribute("src");

                    // Price selector
                    String productPriceStr = product.findElement(By.cssSelector("span.a-price")).getText().replace("\n", ".");
                    String priceValue = productPriceStr.replaceAll("[^0-9.]+", "");
                    double productPrice = 0.0; // Default value if priceValue is empty
                    if (!priceValue.isEmpty()) {
                        productPrice = Double.parseDouble(priceValue);
                    }

                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setName(productName);
                    phoneCase.setPrice(String.valueOf(productPrice));
                    phoneCase.setDescription(productDescription);
                    phoneCase.setWebsiteLink(productLink);
                    phoneCase.setProductImageUrl(productImageSRC);

                    session.save(phoneCase);
                    scrapedItems++;
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    System.err.println("Some elements not found in the product listing. Skipping this product.");
                    skippedItems++;
                }
            }
        }
        // Commit the transaction and close the Hibernate session
        session.getTransaction().commit();
        session.close();

        // Close the browser
        driver.quit();

        // Log the summary
        int totalItems = scrapedItems + skippedItems;
        System.out.println("Amazon Scraped items: " + scrapedItems);
        System.out.println("Amazon Skipped items: " + skippedItems);
        System.out.println("Amazon Total items: " + totalItems);
    }
}

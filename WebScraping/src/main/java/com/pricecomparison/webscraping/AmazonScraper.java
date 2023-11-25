package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.DatabaseUtil;
import com.pricecomparison.util.ExtractProductPrice;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;

public class AmazonScraper extends Thread {
    private final WebDriver driver;
    private final SessionFactory sessionFactory;

    private static final int MAX_PAGES = 1;

    // Constructor to inject WebDriver
    public AmazonScraper(WebDriver driver, SessionFactory sessionFactory) {
        this.driver = driver;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        // Initialize session
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        // Iterate over multiple pages
        boolean newDataSaved = false;
        for (int page = 1; page <= MAX_PAGES; page++) {
            String url = "https://www.amazon.co.uk/s?k=iPhone+case&page=" + page;
            driver.get(url);

            acceptCookies(driver);

            // Get all product links on the page
            List<WebElement> productLinks = driver.findElements(By.cssSelector("a.a-link-normal.s-no-outline"));

            // Collect all product URLs
            List<String> productUrls = new ArrayList<>();
            for (WebElement productLink : productLinks) {
                productUrls.add(productLink.getAttribute("href"));
            }

            // Iterate through each product URL
            for (String productUrl : productUrls) {
                if (DatabaseUtil.isDataExists(session, "SELECT COUNT(*) FROM PriceComparison WHERE url = :URL", "URL", productUrl)) {
                    System.out.println("Data already exists for URL: " + productUrl);
                    continue;
                }
                try {
                    // Navigate to the product page
                    driver.get(productUrl);

                    // Scrape product information
                    String productName = driver.findElement(By.cssSelector("span#productTitle.a-size-large.product-title-word-break")).getText();
                    String productPrice = ExtractProductPrice.price(driver);
                    String productColour = driver.findElement(By.cssSelector("table tbody tr.po-color td span.po-break-word")).getText();
                    String productModels = driver.findElement(By.cssSelector("table tbody tr.po-compatible_phone_models td span.po-break-word")).getText();
                    String productImageURL = driver.findElement(By.cssSelector("span.a-declarative div img#landingImage.a-dynamic-image")).getAttribute("src");

                    // Create and save PhoneCase entity
                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setPhoneModel(productModels);
                    session.persist(phoneCase);

                    // Create and save PhoneCaseVariation entity
                    PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();
                    phoneCaseVariation.setPhoneCase(phoneCase);
                    phoneCaseVariation.setColor(productColour);
                    phoneCaseVariation.setImageUrl(productImageURL);
                    session.persist(phoneCaseVariation);

                    // Create and save PriceComparison entity
                    PriceComparison priceComparison = new PriceComparison();
                    priceComparison.setCaseVariant(phoneCaseVariation);
                    priceComparison.setName(productName);
                    priceComparison.setPrice(productPrice.substring(1));
                    priceComparison.setUrl(productUrl);

                    // Set PriceComparison in PhoneCaseVariation
                    phoneCaseVariation.setPriceComparison(priceComparison);
                    session.persist(priceComparison);

                    newDataSaved = true;

                    try {
                        Thread.sleep(2000); // Sleep for 2 seconds between iterations
                    } catch (InterruptedException e) {
                        System.out.println("Error sleeping thread." + e.getMessage());
                    }

                } catch (WebDriverException e) {
                    // Handle WebDriver exception, e.g., log the error
                    System.err.println("WebDriverException: " + e.getMessage());
                    // Wait for a while and retry the operation
                    try {
                        Thread.sleep(5000); // 5 seconds
                    } catch (InterruptedException ex) {
                        System.out.println("Error sleeping thread." + ex.getMessage());
                    }
                    // Retry the operation
                    continue; // Skip the rest of the loop and move to the next iteration
                }

                // Navigate back to the search results page
                driver.navigate().back();
            }
        }

        // Commit the transaction and close the Hibernate session
        if (newDataSaved) {
            session.getTransaction().commit();
        } else {// If no new data was saved, rollback the transaction
            session.getTransaction().rollback();
        }
        session.close();

        // Close the browser
        driver.quit();
        System.out.println("✔ AmazonScraper thread finished scraping.");
    }

    private void acceptCookies(WebDriver driver) {
        try {
            WebElement cookiesButton = driver.findElement(By.id("sp-cc-accept"));
            cookiesButton.click();
            System.out.println("Amazon Accept Cookies button clicked.");
        } catch (Exception e) {
            System.err.println("Amazon Accept Cookies button not found. Continuing without clicking it.");
        }
    }
}
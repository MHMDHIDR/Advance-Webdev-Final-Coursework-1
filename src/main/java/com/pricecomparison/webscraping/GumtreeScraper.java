package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.ExtractProductModel;
import com.pricecomparison.util.ExtractProductPrice;
import com.pricecomparison.util.HibernateUtil;

import org.hibernate.Session;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;


public class GumtreeScraper extends Thread {
    @Override
    public void run() {
        // Set up your Java project and configure Selenium
        System.setProperty("webdriver.chrome.driver", "/Users/mhmdhidr/chromedriver/chromedriver");
        WebDriver driver = new ChromeDriver();

        // Initialize Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        boolean cookiesAccepted = false;

        // Iterate over multiple pages
        for (int page = 1; page <= 5; page++) {
            String url = "https://www.gumtree.com/search?q=iphone+case&page=" + page;
            driver.get(url);

            if (!cookiesAccepted) {
                acceptCookies(driver);
                cookiesAccepted = true;
            }

            // Get all product links on the page
            List<WebElement> productLinks = driver.findElements(By.cssSelector("a.e25keea19[data-q='search-result-anchor']"));

            // Collect all product URLs
            List<String> productUrls = new ArrayList<>();
            for (WebElement productLink : productLinks) {
                productUrls.add(productLink.getAttribute("href"));
            }

            // Iterate through each product URL
            for (String productUrl : productUrls) {
                try {
                    // Navigate to the product page
                    driver.get(productUrl);

                    // Scrape product information
                    String productName = driver.findElement(By.cssSelector("h1.css-4rz76v[data-q='vip-title']")).getText();
                    String productPrice = ExtractProductPrice.price(driver);
                    String productModels = ExtractProductModel.model(productName);
                    String productImageURL = driver.findElement(By.cssSelector("ul li.active.carousel-item[data-testid='slider'] img")).getAttribute("src");

                    // Create and save PhoneCase entity
                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setPhoneModel(productModels);
                    session.save(phoneCase);

                    // Create and save PhoneCaseVariation entity
                    PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();
                    phoneCaseVariation.setPhoneCase(phoneCase);
                    phoneCaseVariation.setColor("Click on \"View Details\" to see the color");
                    phoneCaseVariation.setImageUrl(productImageURL);
                    session.save(phoneCaseVariation);

                    // Create and save PriceComparison entity
                    PriceComparison priceComparison = new PriceComparison();
                    priceComparison.setCaseVariant(phoneCaseVariation);
                    priceComparison.setPrice(productPrice.substring(1)); // Remove the '£' symbol
                    priceComparison.setUrl(productUrl);

                    // Set PriceComparison in PhoneCaseVariation
                    phoneCaseVariation.setPriceComparison(priceComparison);

                    session.save(priceComparison);

                    try {
                        Thread.sleep(2000); // Sleep for 2 seconds between iterations
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
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
        session.getTransaction().commit();
        session.close();

        // Close the browser
        driver.quit();
        System.out.println("✔ AmazonScraper thread finished scraping.");
    }

    private void acceptCookies(WebDriver driver) {
        try {
            WebElement cookiesButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
            cookiesButton.click();
            System.out.println("Gumtree Accept Cookies button clicked.");
        } catch (Exception e) {
            System.err.println("Gumtree Accept Cookies button not found. Continuing without clicking it.");
        }
    }
}

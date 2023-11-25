package com.pricecomparison.webscraping;

import com.pricecomparison.util.DatabaseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class eBayScraper extends Thread {
    private final WebDriver driver;
    private final SessionFactory sessionFactory;

    private static final int MAX_PAGES = 1;

    public eBayScraper(SessionFactory sessionFactory) {
        this.driver = new ChromeDriver();
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        // Initialize session
        try (Session session = sessionFactory.openSession()) {

            for (int page = 1; page <= MAX_PAGES; page++) {
                String url = "https://www.ebay.co.uk/sch/i.html?_nkw=iPhone+case&_pgn=" + page;
                driver.get(url);

                // Get all product links on the page
                List<WebElement> productLinks = driver.findElements(By.cssSelector("a.s-item__link"));

                // Collect all product URLs
                List<String> productUrls = new ArrayList<>();
                for (WebElement productLink : productLinks) {
                    productUrls.add(productLink.getAttribute("href"));
                }

                // Iterate through each product URL
                for (int i = 1; i < productLinks.size(); i++) {
                    WebElement productLink = productLinks.get(i);
                    String productUrl = productLink.getAttribute("href");

                    if (DatabaseUtil.isDataExists(session, "SELECT COUNT(*) FROM PriceComparison WHERE url = :URL", "URL", productUrl)) {
                        System.out.println("Data already exists for URL: " + productUrl);
                        continue;
                    }

                    try {
                        // Navigate to the product page
                        driver.get(productUrl);

                        // Scrape product information
                        String productName = driver.findElement(By.cssSelector("h1.x-item-title__mainTitle .ux-textspans--BOLD")).getText();
                        String productPrice = driver.findElement(By.cssSelector(".x-bin-price__content div span.ux-textspans")).getText();
                        String imageElement = driver.findElement(By.cssSelector("div[data-idx='0'] img")).getAttribute("src");

                        System.out.println("productUrl: " + productUrl);
                        System.out.println("Product Name: " + productName);
                        System.out.println("Product Price: " + productPrice);
                        System.out.println("Product Image URL: " + imageElement);
                        System.out.println("Product phoneModels: " + extractPhoneModels());
                        System.out.println("Product phoneColours: " + extractPhoneColours());
                        System.out.println("=============================================");

                    } catch (WebDriverException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }

                    // Navigate back to the search results page
                    driver.navigate().back();

                    // Re-fetch the product links after navigating back
                    productLinks = driver.findElements(By.cssSelector("a.s-item__link"));
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // Close the browser
        driver.quit();
        System.out.println("✔ eBayScraper thread finished scraping.");
    }

    private List<String> extractPhoneInfo(List<String> selectors, String defaultInfo) {
        for (String selector : selectors) {
            try {
                List<WebElement> selectElements = driver.findElements(By.cssSelector(selector));

                if (!selectElements.isEmpty()) {
                    List<String> phoneInfo = new ArrayList<>();

                    for (WebElement selectElement : selectElements) {
                        List<WebElement> optionElements = selectElement.findElements(By.tagName("option"));
                        for (int j = 1; j < optionElements.size(); j++) {
                            phoneInfo.add(optionElements.get(j).getText());
                        }
                    }

                    return phoneInfo;
                }
            } catch (WebDriverException ignored) {}
        }

        // If none of the selectors match, return the default value
        List<String> defaultList = new ArrayList<>();
        defaultList.add(defaultInfo);
        return defaultList;
    }

    private List<String> extractPhoneColours() {
        List<String> colorSelectors = Arrays.asList(
            ".x-msku__select-box[selectboxlabel='Case Colour']",
            ".x-msku__select-box[selectboxlabel='Color']"
        );
        return extractPhoneInfo(colorSelectors, "Clear");
    }

    private List<String> extractPhoneModels() {
        List<String> modelsSelectors = Arrays.asList(
            ".x-msku__select-box[selectboxlabel='iPhone Model']",
            ".x-msku__select-box[selectboxlabel='Compatible Model']",
            ".x-msku__select-box[selectboxlabel='Model']"
        );
        return extractPhoneInfo(modelsSelectors, "N/A");
    }

}

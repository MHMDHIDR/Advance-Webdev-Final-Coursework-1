package com.pricecomparison.webscraping;

import com.pricecomparison.util.Const;
import com.pricecomparison.util.ExtractProductModel;
import com.pricecomparison.util.ExtractProductPrice;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GumtreeScraper extends WebScrapper {
    private static final String WEBSITE = "Gumtree";

    @Override
    public void run() {
        // Initialize the WebDriver
        WebDriver driver = getDriver();

        boolean cookiesAccepted = false;

        try {
            // Iterate over multiple pages
            for (int page = 1; page <= Const.MAX_PAGES; page++) {
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
                        String productColour = getPhoneCaseColour(productName);
                        String productModels = ExtractProductModel.model(productName);
                        String productImageURL = driver.findElement(By.cssSelector("ul li.active.carousel-item[data-testid='slider'] img")).getAttribute("src");

                        caseDao.printData(
                                productUrl,
                                productName,
                                productPrice,
                                productImageURL,
                                productModels,
                                productColour
                        );

    //                    String[] models = caseDao.getModels(productModels);
    //                    ArrayList<PhoneCase> cases = new ArrayList<>();
    //                    for (String model : models) {
    //                        model = caseDao.filtered(model);
    //
    //                        if (!caseDao.isFilteredAndChecked(model)) {
    //                            continue;
    //                        }
    //                        // Create PhoneCase object and save it to the database
    //                        caseDao.saveCase(cases, model);
    //                    }
    //
    //                    ArrayList<PhoneCaseVariation> variants = new ArrayList<>();
    //                    for (PhoneCase phoneCase : cases) {
    //                        // Create PhoneCase object and save it to the database
    //                        caseDao.saveVariant(variants, phoneCase, productColour, productImageURL);
    //                    }
    //
    //                    for (PhoneCaseVariation phoneCaseVariation : variants) {
    //                        // Create PhoneCase object and save it to the database
    //                        caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productUrl);
    //                    }

    //                    try {
    //                        Thread.sleep(2000); // Sleep for 2 seconds between iterations
    //                    } catch (InterruptedException e) {
    //                        System.out.println("Error sleeping thread." + e.getMessage());
    //                    }

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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        System.out.println("✔ GumtreeScraper thread finished scraping.");
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

    //get color from the product name
    private String getPhoneCaseColour(String productName) {
        // Use regex to find the last dash and extract color
        Pattern pattern = Pattern.compile(".*\\s-\\s(.*)$");
        Matcher matcher = pattern.matcher(productName);

        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            // If no dash is found, return default message
            return "N/A";
        }
    }
}
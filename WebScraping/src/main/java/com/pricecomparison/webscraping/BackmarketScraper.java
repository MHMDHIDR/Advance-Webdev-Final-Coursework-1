package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.ExtractProductModel;
import com.pricecomparison.util.Const;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

/**
 * BackmarketScraper class extends WebScrapper class
 * and implements the run method.
 * It scrapes the Backmarket website for phone cases.
 */
public class BackmarketScraper extends WebScrapper {
    private static final String WEBSITE = "Backmarket";

    @Override
    public void run() {
        // Initialize the WebDriver
        WebDriver driver = getDriver();
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        try {
            // Iterate over multiple pages
            for (int page = 1; page <= Const.MAX_PAGES; page++) {
                String url = "https://www.backmarket.co.uk/en-gb/search?q=iphone_case&page=" + page;
                driver.get(url);
                acceptCookies(driver);

                // Find and process each product on the page
                List<WebElement> productLinks = driver.findElements(By.cssSelector(".productCard a"));

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
                        acceptCookies(driver);

                        // Sleep randomly for 1-2.5 seconds
                        try {
                            Thread.sleep((long) (Math.random() * 1500 + 1000));
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }

                        // Scrape product information
                        String productName = driver.findElement(By.cssSelector(".justify-between.mb-5.md\\:flex > h1")).getText();
                        String productPrice = driver.findElement(By.cssSelector("[data-test='normal-price']:not(.text-primary)")).getText().replace("£", "");
                        String productImageURL = driver.findElement(By.cssSelector("li:nth-child(1) > img")).getAttribute("src");
                        String productModels = "";
                        String productColour = "";

                        try { Thread.sleep(1000); }
                        catch (InterruptedException e) { e.printStackTrace(); }

                        // Click on productSpecifications button to get the productModels and productColour
                        jsExecutor.executeScript("document.querySelector('.max-w-full div:nth-child(4) li:nth-child(1) > button').click()");

                        try {
                            Thread.sleep(2000);
                            System.out.println("Got model and color from specifications");
                            productModels = driver.findElement(By.cssSelector("li:nth-child(1) .text-right .whitespace-nowrap")).getText().replace("Pack Case", "");
                            productModels = !productModels.toLowerCase().startsWith("iphone") ? ExtractProductModel.model(productModels) : productModels;

                            productColour = driver.findElement(By.cssSelector("li:nth-child(2) .text-right .whitespace-nowrap")).getText();
                            productColour = productColour.equals("Transparent") ? "Clear" : productColour;
                        } catch (org.openqa.selenium.NoSuchElementException error) {
                            System.err.println("--Got the Color from ProductName--");
                            productModels = ExtractProductModel.model(productName);
                            productColour = extractColor(productName).equals("Transparent") ? "Clear" : extractColor(productName);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        caseDao.printData(
                            productUrl,
                            productName,
                            productPrice,
                            productImageURL,
                            productModels,
                            productColour
                        );

                        String[] models = caseDao.getModels(productModels);
                        ArrayList<PhoneCase> cases = new ArrayList<>();
                        for (String model : models) {
                            model = caseDao.filtered(model);

                            if (!caseDao.isFilteredAndChecked(model)) {
                                continue;
                            }
                            // Create PhoneCase object and save it to the database
                            caseDao.saveCase(cases, model);
                        }

                        ArrayList<PhoneCaseVariation> variants = new ArrayList<>();
                        for (PhoneCase phoneCase : cases) {
                            // Create PhoneCase object and save it to the database
                            caseDao.saveVariant(variants, phoneCase, productColour, productImageURL);
                        }

                        for (PhoneCaseVariation phoneCaseVariation : variants) {
                            // Create PhoneCase object and save it to the database
                            caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productUrl);
                        }

                    } catch (WebDriverException e) {
                        e.printStackTrace();
                        continue; // Skip the rest of the loop and move to the next iteration
                    }

                    // Navigate back to the search results page
                    //driver.navigate().back();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        System.out.println("✔ BackmarketScraper thread finished scraping.");
    }

    /**
     * Accept cookies is a method to click on accept cookies
     * button if the button is found, then print a message
     * @param driver the WebDriver
     */
    private void acceptCookies(WebDriver driver) {
        try {
            WebElement cookiesButton = driver.findElement(By.cssSelector("[data-qa=\"accept-cta\"]"));
            cookiesButton.click();
            System.out.println("BackMarket Accept Cookies button clicked.");
        } catch (Exception e) {
            System.err.println("BackMarket Accept Cookies button not found. Continuing without clicking it.");
        }
    }

    /**
     * get the color from ProductName after the last dash (-)
     * if there is no dash, return the string "N/A"
     * @param productName the name of the product
     * @return the color of the product
     */
    private String extractColor(String productName) {
        return productName.contains("-") ? productName.substring(productName.lastIndexOf("-") + 1).trim() : "N/A";
    }
}
package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;

import com.pricecomparison.util.ExtractProductPrice;
import com.pricecomparison.util.Const;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;


public class AmazonScraper extends WebScrapper {
    private static final String WEBSITE = "Amazon";


    @Override
    public void run() {
        WebDriver driver = getDriver();

        // Iterate over multiple pages
        for (int page = 1; page <= Const.MAX_PAGES; page++) {
            String url = "https://www.amazon.co.uk/s?k=iPhone+case&page=" + page;
            driver.get(url);

            //must accept cookies
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
                try {
                    // Navigate to the product page
                    driver.get(productUrl);

                    //wait random time between 1 and 3 seconds
                    try {
                        Thread.sleep((long) (Math.random() * 2000 + 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Scrape product information
                    String productName = driver.findElement(By.cssSelector("span#productTitle.a-size-large.product-title-word-break")).getText();
                    String productPrice = ExtractProductPrice.price(driver);
                    String productColour = driver.findElement(By.cssSelector("table tbody tr.po-color td span.po-break-word")).getText();
                    String productModels = driver.findElement(By.cssSelector("tr.po-compatible_phone_models .a-span9 .a-size-base")).getText();
                    String productImageURL = driver.findElement(By.cssSelector("span.a-declarative div img#landingImage.a-dynamic-image")).getAttribute("src");


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
                    for (PhoneCase phoneCase: cases) {
                        // Create PhoneCase object and save it to the database
                        caseDao.saveVariant(variants, phoneCase, productColour, productImageURL);
                    }

                    for (PhoneCaseVariation phoneCaseVariation: variants) {
                        // Create PhoneCase object and save it to the database
                        caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productUrl);
                    }
                } catch (WebDriverException e) {
                    System.err.println(e.getMessage());
                    // Navigate back to the search results page
                    continue;
                }

                // Navigate back to the search results page
                driver.navigate().back();
            }
        }


        // Close the browser
        driver.quit();
        System.out.println("✔ AmazonScraper thread finished scraping.");
    }


    private void acceptCookies(WebDriver driver) {
        try {
            WebElement cookiesButton = driver.findElement(By.id("sp-cc-accept"));
            cookiesButton.click();
            System.out.println("Amazon Cookies button clicked.");
        } catch (Exception e) {
            System.err.println("Amazon Cookies Not found. Continuing without clicking it.");
        }
    }
}
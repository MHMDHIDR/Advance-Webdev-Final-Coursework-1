package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.CurrencyConverter;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class BestBuyScraper extends WebScrapper {
    private static final String WEBSITE = "BestBuy";

    @Override
    public void run() {
        // Initialize the WebDriver
        WebDriver driver = getDriver();
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        try {
            for (int page = 1; page <= Const.MAX_PAGES; page++) {
                String bestBuyUrl = "https://www.bestbuy.com/site/searchpage.jsp?st=iPhone+case&intl=nosplash&cp=" + page;
                driver.get(bestBuyUrl);

                // Find and process each product on the page
                List<WebElement> productLinks = driver.findElements(By.cssSelector(".sku-item[data-sku-id] .column-left a[tabindex]"));

                // Collect all product URLs
                List<String> productInPageHrefs = new ArrayList<>();
                for (WebElement productLink : productLinks) {
                    productInPageHrefs.add(productLink.getAttribute("href"));
                }

                for (String productInPageHref : productInPageHrefs) {
                    try {
                        driver.get(productInPageHref);

                        // Sleep randomly for 1-1.5 seconds
                        try {
                            Thread.sleep((long) (Math.random() * 500 + 1000));
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }

                        String productName = driver.findElement(By.cssSelector(".sku-title h1.heading-5")).getText();
                        String productPriceUSD = driver.findElement(By.cssSelector(".priceView-customer-price span[aria-hidden='true']")).getText();
                        String productPrice = CurrencyConverter.convertToGBP(productPriceUSD).replaceAll("[^\\d.]", "");
                        String productImageURL = driver.findElement(By.cssSelector(".primary-button img")).getAttribute("src");
                        String productModels = "";
                        String productColour = "";

                        try { Thread.sleep(2000); }
                        catch (InterruptedException e) { e.printStackTrace(); }

                        jsExecutor.executeScript("document.querySelector('button.specifications-drawer-btn[data-testid]').click()");
                        try {
                            productModels = driver.findElement(By.cssSelector("ul:nth-child(1) div:nth-child(3) div.description")).getText();
                            productColour = driver.findElement(By.cssSelector("ul:nth-child(2) div:nth-child(8) div.description")).getText();
                        } catch (org.openqa.selenium.NoSuchElementException e) {
                            productModels = "N/A";
                            productColour = extractColor(productName);
                        }

                        System.out.println("Product URL: " + productInPageHref);
                        System.out.println("Product Name: " + productName);
                        System.out.println("productPrice: " + productPrice);
                        System.out.println("productImageURL: " + productImageURL);
                        System.out.println("productModels: " + productModels);
                        System.out.println("productColour: " + productColour);

                        // Click on productSpecifications button
//                        WebElement specificationsButton = driver.findElement(By.cssSelector("button.specifications-drawer-btn[data-testid]"));
//                        if (specificationsButton != null) {
//                            specificationsButton.click();
//
//                            try { Thread.sleep((long) (Math.random() * 1000 + 500)); }
//                            catch (InterruptedException e) { System.out.println(e.getMessage()); }
//
//                            try {
//                                productModels = driver.findElement(By.cssSelector("ul:nth-child(1) div:nth-child(3) div.description")).getText();
//                                productColour = driver.findElement(By.cssSelector("ul:nth-child(2) div:nth-child(8) div.description")).getText();
//                            } catch (org.openqa.selenium.NoSuchElementException e) {
//                                productModels = "N/A";
//                                productColour = extractColor(productName);
//                            }
//                        }
//
//                        String[] models = caseDao.getModels(productModels);
//                        ArrayList<PhoneCase> cases = new ArrayList<>();
//                        for (String model : models) {
//                            model = caseDao.filtered(model);
//
//                            if (!caseDao.isFilteredAndChecked(model)) {
//                                continue;
//                            }
//                            // Create PhoneCase object and save it to the database
//                            caseDao.saveCase(cases, model);
//                        }
//
//                        ArrayList<PhoneCaseVariation> variants = new ArrayList<>();
//                        for (PhoneCase phoneCase : cases) {
//                            // Create PhoneCase object and save it to the database
//                            caseDao.saveVariant(variants, phoneCase, productColour, productImageURL);
//                        }
//
//                        for (PhoneCaseVariation phoneCaseVariation : variants) {
//                            // Create PhoneCase object and save it to the database
//                            caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productInPageHref);
//                        }

                    } catch (WebDriverException e) {
                        System.err.println(e.getMessage());
                        continue;
                    }

                    driver.navigate().back();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        System.out.println("✔ BestBuyScraper Thread finished scraping.");
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

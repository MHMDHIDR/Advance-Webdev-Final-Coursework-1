package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.CurrencyConverter;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

/**
 * BestBuyScraper class extends WebScrapper class
 * and implements the run method.
 * It scrapes the BestBuy website for phone cases.
 *
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class BestBuyScraper extends WebScrapper {
    private static final String WEBSITE = "BestBuy";

    @Override
    public void run() {
        // Initialize the WebDriver
        WebDriver driver = getDriver();
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;

        try {
            //should find 18 Items on the first page
            for (int page = 1; page <= Const.MAX_PAGES; page++) {
                String bestBuyUrl = "https://www.bestbuy.com/site/searchpage.jsp?st=iPhone+case&intl=nosplash&cp=" + page;
                driver.get(bestBuyUrl);

                // Find and process each product on the page
                List<WebElement> productLinks = driver.findElements(By.cssSelector(".sku-item[data-sku-id] .column-left a[tabindex]"));

                // Collect all product URLs
                List<String> productUrls = new ArrayList<>();
                for (WebElement productLink : productLinks) {
                    productUrls.add(productLink.getAttribute("href"));
                }

                for (String productUrl : productUrls) {
                    try {
                        driver.get(productUrl);

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

                        try { Thread.sleep(1000); }
                        catch (InterruptedException e) { e.printStackTrace(); }

                        // Click on productSpecifications button to get the productModels and productColour
                        jsExecutor.executeScript("document.querySelector('button.specifications-drawer-btn[data-testid]').click()");

                        try {
                            Thread.sleep(2000);
                            System.out.println("Got model and color from specifications");
                            productModels = driver.findElement(By.cssSelector("ul:nth-child(1) div:nth-child(3) div.description")).getText();
                            productColour = driver.findElement(By.cssSelector("ul:nth-child(2) div:nth-child(8) div.description")).getText();
                        } catch (NoSuchElementException error) {
                            System.err.println("--Got the Color from ProductName--");
                            productModels = driver.findElement(By.cssSelector("ul:nth-child(1) div:nth-child(3) div.description")).getText();
                            productColour = extractColor(productName);
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


                        String[] models = caseDao.getModels(productModels) == null ? new String[]{productModels} : caseDao.getModels(productModels);
                        List<PhoneCase> cases = new ArrayList<>();
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
                        System.err.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            quitDriver();
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

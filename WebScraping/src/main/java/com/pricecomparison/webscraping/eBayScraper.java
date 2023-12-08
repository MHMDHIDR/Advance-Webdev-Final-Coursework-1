package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.Const;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * eBayScraper class extends WebScrapper class
 * and implements the run method.
 * It scrapes the eBay website for phone cases.
 */
public class eBayScraper extends WebScrapper {
    private static final String WEBSITE = "eBay";

    // Initialize the WebDriver
    WebDriver driver = getDriver();

    @Override
    public void run() {
        try {
            for (int page = 1; page <= Const.MAX_PAGES; page++) {
                String url = "https://www.ebay.co.uk/sch/i.html?_nkw=iPhone+case&_pgn=" + page;
                driver.get(url);

                // Get all product links on the page
                List<WebElement> productLinks = driver.findElements(By.cssSelector("a.s-item__link"));

                // Iterate through each product URL
                for (int i = 1; i < productLinks.size(); i++) {
                    WebElement productLink = productLinks.get(i);
                    String productUrl = productLink.getAttribute("href");

                    try {
                        // Navigate to the product page
                        driver.get(productUrl);

                        // Scrape product information
                        String productName = driver.findElement(By.cssSelector("h1.x-item-title__mainTitle .ux-textspans--BOLD")).getText();
                        String productPrice = driver.findElement(By.cssSelector(".x-bin-price__content div span.ux-textspans")).getText().replace("each", "").replace("£", "");
                        String imageElement = driver.findElement(By.cssSelector("div[data-idx='0'] img")).getAttribute("src");
                        List<String> phoneModels = extractPhoneModels();
                        List<String> phoneColours = extractPhoneColours();

                        caseDao.printData(
                            productUrl,
                            productName,
                            productPrice,
                            imageElement,
                            phoneModels.toString(),
                            phoneColours.toString()
                        );

                        // Apply model checking
                        String[] models = caseDao.getModels(phoneModels.toString());
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
                            for (String color : phoneColours) {
                                // Create PhoneCase object and save it to the database
                                caseDao.saveVariant(variants, phoneCase, color, imageElement);
                            }

                            for (PhoneCaseVariation phoneCaseVariation: variants) {
                                // Create PhoneCase object and save it to the database
                                caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productUrl);
                            }
                        }
                    } catch (WebDriverException e) {
                        System.out.println(e.getMessage());
                    } finally {
                        // Navigate back to the search results page
                        driver.navigate().back();
                        // Re-fetch the product links after navigating back
                        productLinks = driver.findElements(By.cssSelector("a.s-item__link"));
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
            System.out.println("✔ eBayScraper thread finished scraping.");
    }


    /**
     * Extracts the phone models from the given selectors
     * @return @extractPhoneInfo() that returns The phone models List
     */
    private List<String> extractPhoneModels() {
        List<String> modelsSelectors = Arrays.asList(
                ".x-msku__select-box[selectboxlabel='iPhone Model']",
                ".x-msku__select-box[selectboxlabel='Compatible Model']",
                ".x-msku__select-box[selectboxlabel='Model']",
                ".x-msku__select-box[selectboxlabel='MODEL']",
                ".x-msku__select-box[selectboxlabel='Device']"
        );
        return extractPhoneInfo(modelsSelectors, "N/A");
    }

    /**
     * Extracts the phone colours from the given selectors
     * @return The phone colours List
     */
    private List<String> extractPhoneColours() {
        List<String> colorSelectors = Arrays.asList(
                ".x-msku__select-box[selectboxlabel='Case Colour']",
                ".x-msku__select-box[selectboxlabel='Color']"
        );
        return extractPhoneInfo(colorSelectors, "Clear");
    }

    /**
     * Extracts the phone information from the given selectors
     * @param selectors The selectors to use to extract the phone information
     * @param defaultInfo The default information to return if none of the selectors match
     * @return The phone information List
     */
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
}

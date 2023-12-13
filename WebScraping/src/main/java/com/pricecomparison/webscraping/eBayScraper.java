package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.Const;

import org.openqa.selenium.*;

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

                // Collect all product URLs and remove the first one because it is not a product
                List<String> productUrls = new ArrayList<>();
                for (WebElement productLink : productLinks) {
                    productUrls.add(productLink.getAttribute("href"));
                }
                productUrls.remove(0);

                // Iterate through each product URL
                for (String productUrl : productUrls) {
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
                        String[] models = caseDao.getModels(phoneModels.toString())  == null ? new String[]{phoneModels.toString()} : caseDao.getModels(phoneModels.toString());
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
                            for (String color : phoneColours) {
                                // Create PhoneCase object and save it to the database
                                caseDao.saveVariant(variants, phoneCase, color, imageElement);
                            }

                            for (PhoneCaseVariation phoneCaseVariation: variants) {
                                // Create PhoneCase object and save it to the database
                                caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productUrl);
                            }
                        }
                    } catch (NoSuchElementException error) {
                        System.out.println("⚠ NoSuchElementException: " + error.getMessage());
                    } catch (TimeoutException exTimeout) {
                        System.out.println("⚠ TimeoutException: " + exTimeout.getMessage());
                    } catch (WebDriverException e) {
                        e.printStackTrace();
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
            quitDriver();
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
        return extractPhoneInfo(modelsSelectors, "","N/A");
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
        return extractPhoneInfo(colorSelectors, "","Clear");
    }

    /**
     * Extracts the phone information from the given selectors
     * @param selectors The selectors to use to extract the phone information
     * @param defaultInfo The default information to return if none of the selectors match
     * @return The phone information List
     */
    public List<String> extractPhoneInfo(List<String> selectors, String tagName, String defaultInfo) {
        tagName = tagName == null || tagName.isEmpty() ? "option" : tagName;

        for (String selector : selectors) {
            try {
                List<WebElement> selectElements = driver.findElements(By.cssSelector(selector));

                if (!selectElements.isEmpty()) {
                    List<String> phoneInfo = new ArrayList<>();

                    for (WebElement selectElement : selectElements) {
                        List<WebElement> optionElements = selectElement.findElements(By.tagName(tagName));
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

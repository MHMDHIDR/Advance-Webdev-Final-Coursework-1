package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.ExtractProductModel;
import com.pricecomparison.util.ExtractProductPrice;
import com.pricecomparison.util.Const;

import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <h1>AmazonScraper class extends WebScrapper class</h1>
 * and implements the run method.
 * <p>
 * It scrapes the Amazon website for phone cases.
 *
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class AmazonScraper extends WebScrapper {
    private static final String WEBSITE = "Amazon";

    eBayScraper eBayScraper = new eBayScraper();

    @Override
    public void run() {
        // Initialize the WebDriver
        WebDriver driver = getDriver();

        try {
            /* Iterate through each page of the search results
             * and scrape the product information
             */

            //should find 31 Items on the first page
            for (int page = 1; page <= Const.MAX_PAGES; page++) {
                String url = "https://www.amazon.co.uk/s?k=iPhone+case&page=" + page;
                driver.get(url);
                sleep(3000);

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

                        // Scrape product information
                        String productName = driver.findElement(By.cssSelector("span#productTitle.a-size-large.product-title-word-break")).getText();
                        String productPrice = ExtractProductPrice.price(driver).replace("£", "");
                        String productImageURL = driver.findElement(By.cssSelector("span.a-declarative div img#landingImage.a-dynamic-image")).getAttribute("src");
                        String productModels = Objects.equals(extractPhoneModels().toString(), "N/A") ? ExtractProductModel.model(productName) : extractPhoneModels().toString();
                        String productColour = driver.findElement(By.cssSelector("table tbody tr.po-color td span.po-break-word")).getText();
                        productColour = productColour.equals("Transparent") ? "Clear" : productColour;

                        caseDao.printData(
                            productUrl,
                            productName,
                            productPrice,
                            productImageURL,
                            productModels,
                            productColour
                        );

                        String[] models =  caseDao.getModels(productModels)  == null ? new String[] {productModels} : caseDao.getModels(productModels);
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

        System.out.println("✔ AmazonScraper thread finished scraping.");
    }

    private List<String> extractPhoneModels() {
        List<String> modelsSelectors = Arrays.asList(
                ".po-compatible_phone_models td .po-break-word",
                "#inline-twister-expanded-dimension-text-size_name"
        );
        return eBayScraper.extractPhoneInfo(modelsSelectors, "span", "N/A");
    }
}
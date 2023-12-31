package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.ExtractProductModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
// Import Selenium and using it to execute JavaScript
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ArgosScraper class extends WebScrapper class
 * and implements the run method.
 * It scrapes the Argos website for phone cases.
 *
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class ArgosScraper extends WebScrapper {
    private static final String WEBSITE = "Argos";

    /**
     * This is the constructor of the ArgosScraper class.
     */
    public ArgosScraper() {
        super();
    }

    @Override
    public void run() {
        // Initialize the WebDriver
        WebDriver driver = getDriver();

        try {
            // if <= Const.MAX_PAGES is less than or equals to 4 give me Const.MAX_PAGES
            // otherwise give me 4 argos scraper only has 4 pages because I don't have
            // more phone cases after page 4
            for (int page = 1; page <= (Math.min(Const.MAX_PAGES, 4)); page++) {
                String argosUrl = "https://www.argos.co.uk/search/iphone-case/opt/page:" + page;
                // if <= Const.MAX_PAGES is less than or equals to 6 give me Const.MAX_PAGES
                driver.get(argosUrl);

                // Scroll to the bottom of the page
                JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                jsExecutor.executeScript("window.scrollTo({top: (document.body.scrollHeight/2), behavior: 'smooth'})");
                //sleep for 2 seconds
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jsExecutor.executeScript("window.scrollTo({top: (document.body.scrollHeight), behavior: 'smooth'})");
                //sleep for 2 seconds
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Use JSoup to parse the pageSource
                String pageSource = driver.getPageSource();
                Document doc = Jsoup.parse(pageSource);

                // Find and process each product on the page
                Elements productElements = doc.select("div[data-test='component-product-card']");

                for (Element product : productElements) {
                    String productName = product.select("div[data-test='component-product-card-title']").text();
                    String productPrice = product.select("div[data-test='component-product-card-price'] strong").text().replace("£", "");
                    String productLink = "https://www.argos.co.uk/"+product.select("[data-test='component-product-card-textContainer'] a[data-test='component-product-card-title-link']").attr("href");
                    String productImageURL = "https:" + product.select("[data-test=\"component-product-card-imageWrapper\"] img").attr("src");

                    // Extract color from the product name after the last dash ("-") symbol
                    int lastDashIndex = productName.lastIndexOf("-");
                    // If there is no dash, set color to "DefaultColor"
                    String productColour = (lastDashIndex != -1) ? productName.substring(lastDashIndex + 1).trim() : "clear";

                    caseDao.printData(
                        argosUrl,
                        productName,
                        productPrice,
                        productImageURL,
                        Arrays.toString(ExtractProductModel.model(productName).replace("Apple", "").trim().split(",")),
                        productColour
                    );

                    String[] models = ExtractProductModel.model(productName).replace("Apple", "").trim().split(",");
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
                        // Create PhoneCaseVariation object and save it to the database
                        caseDao.saveVariant(variants, phoneCase, productColour, productImageURL);
                    }

                    for (PhoneCaseVariation phoneCaseVariation : variants) {
                        // Create PriceComparison object and save it to the database
                        caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productLink);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            quitDriver();
        }


        System.out.println("✔ ArgosScraper thread finished scraping.");
    }
}


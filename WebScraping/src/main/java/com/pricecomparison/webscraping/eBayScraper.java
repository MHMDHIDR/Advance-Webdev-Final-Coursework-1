package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.SaveModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class eBayScraper extends Thread {
    private final WebDriver driver;
    private final SessionFactory sessionFactory;
    private static final String WEBSITE = "eBay";

    public eBayScraper(SessionFactory sessionFactory) {
        this.driver = new ChromeDriver();
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        // Initialize session
        try (Session session = sessionFactory.openSession()) {
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
                        String productPrice = driver.findElement(By.cssSelector(".x-bin-price__content div span.ux-textspans")).getText().replace("each", "");
                        String imageElement = driver.findElement(By.cssSelector("div[data-idx='0'] img")).getAttribute("src");
                        List<String> phoneModels = extractPhoneModels();
                        List<String> phoneColours = extractPhoneColours();

                        // Apply model checking
                        String[] models = getModels(phoneModels.toString());
                        ArrayList<PhoneCase> cases = new ArrayList<>();
                        for (String model : models) {
                            if (SaveModel.isFilteredAndChecked(model)) {
                                continue;
                            }

                            // Create PhoneCase object and save it to the database
                            SaveModel.save(session, cases, model);
                        }


                        ArrayList<PhoneCaseVariation> variants = new ArrayList<>();
                        for (PhoneCase phoneCase : cases) {
                            for (String color : phoneColours) {
                                PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();

                                // Check if the variant already exists in the cases_variants table
                                List<PhoneCaseVariation> variantList = session.createQuery("FROM PhoneCaseVariation WHERE phoneCase = :MODEL AND color = :COLOR", PhoneCaseVariation.class)
                                        .setParameter("MODEL", phoneCase)
                                        .setParameter("COLOR", color)
                                        .getResultList();

                                if (variantList.isEmpty()) {
                                    // Save the new variant
                                    phoneCaseVariation.setPhoneCase(phoneCase);
                                    phoneCaseVariation.setColor(color);
                                    phoneCaseVariation.setImageUrl(imageElement);

                                    session.beginTransaction();
                                    session.persist(phoneCaseVariation);
                                    session.getTransaction().commit();
                                } else {
                                    phoneCaseVariation = variantList.get(0);
                                }
                                variants.add(phoneCaseVariation);

                                // Save or update the comparison
                                List<PriceComparison> priceComparisons = session.createQuery("FROM PriceComparison WHERE caseVariant = :MODEL AND website = :WEBSITE", PriceComparison.class)
                                        .setParameter("MODEL", phoneCaseVariation)
                                        .setParameter("WEBSITE", WEBSITE)
                                        .getResultList();

                                PriceComparison priceComparison = new PriceComparison();
                                if (!priceComparisons.isEmpty()) {
                                    priceComparison = priceComparisons.get(0);
                                }
                                priceComparison.setCaseVariant(phoneCaseVariation);
                                priceComparison.setName(productName);
                                priceComparison.setWebsite(WEBSITE);
                                priceComparison.setPrice(productPrice.substring(1));
                                priceComparison.setUrl(productUrl);

                                session.beginTransaction();
                                session.merge(priceComparison);
                                session.getTransaction().commit();
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
        } finally {
            // Close the browser
            driver.quit();
            System.out.println("✔ eBayScraper thread finished scraping.");
        }
    }


    private static String[] getModels(String productModels) {
        String[] models = productModels.split("[,/]");
        // Trim each model
        for (int i = 0; i < models.length; i++) {
            models[i] = models[i].trim();
        }
        return models;
    }


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

    private List<String> extractPhoneColours() {
        List<String> colorSelectors = Arrays.asList(
                ".x-msku__select-box[selectboxlabel='Case Colour']",
                ".x-msku__select-box[selectboxlabel='Color']"
        );
        return extractPhoneInfo(colorSelectors, "Clear");
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
}

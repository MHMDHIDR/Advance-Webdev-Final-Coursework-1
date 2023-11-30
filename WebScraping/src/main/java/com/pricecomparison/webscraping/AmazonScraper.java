package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.ExtractProductPrice;
import com.pricecomparison.util.Const;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

import java.util.ArrayList;
import java.util.List;

public class AmazonScraper extends Thread {
    private final WebDriver driver;
    private final SessionFactory sessionFactory;
    private static final String WEBSITE = "Amazon";

    // Constructor to inject WebDriver
    public AmazonScraper(WebDriver driver, SessionFactory sessionFactory) {
        this.driver = driver;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        // Initialize session
        Session session = sessionFactory.openSession();
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

                    // Scrape product information
                    String productName = driver.findElement(By.cssSelector("span#productTitle.a-size-large.product-title-word-break")).getText();
                    String productPrice = ExtractProductPrice.price(driver);
                    String productColour = driver.findElement(By.cssSelector("table tbody tr.po-color td span.po-break-word")).getText();
                    String productModels = driver.findElement(By.cssSelector(".po-compatible_phone_models td .po-break-word")).getText();
                    String productImageURL = driver.findElement(By.cssSelector("span.a-declarative div img#landingImage.a-dynamic-image")).getAttribute("src");


                    String[] models =  getModels(productModels);
                    ArrayList<PhoneCase> cases = new ArrayList<>();
                    for (String model : models) {
                        // remove the word "Apple" from the model
                        model = model.replace("Apple", "").trim();

                        if(model.contains("-") || !model.startsWith("iPhone") || model.equalsIgnoreCase("iphone") || model.matches(".*\\bPro\\b.*\\d.*")) {
                            continue;
                        }

                        List<PhoneCase> caseList = session.createQuery("FROM PhoneCase WHERE phoneModel = :MODEL", PhoneCase.class)
                                .setParameter("MODEL", model)
                                .getResultList();
                        PhoneCase phoneCase = new PhoneCase();
                        if (caseList.isEmpty()) {
                            phoneCase.setPhoneModel(model);
                            session.beginTransaction();
                            session.persist(phoneCase);
                            session.getTransaction().commit();
                        } else {
                            phoneCase = caseList.get(0);
                        }
                        cases.add(phoneCase);
                    }

                    ArrayList<PhoneCaseVariation> variants = new ArrayList<>();
                    for (PhoneCase phoneCase: cases) {
                        List<PhoneCaseVariation> variantList = session.createQuery("FROM PhoneCaseVariation WHERE phoneCase = :MODEL AND color = :COLOR", PhoneCaseVariation.class)
                                .setParameter("MODEL", phoneCase)
                                .setParameter("COLOR", productColour)
                                .getResultList();
                        PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();
                        if (variantList.isEmpty()) {
                            phoneCaseVariation.setPhoneCase(phoneCase);
                            phoneCaseVariation.setColor(productColour);
                            phoneCaseVariation.setImageUrl(productImageURL);

                            session.beginTransaction();
                            session.persist(phoneCaseVariation);
                            session.getTransaction().commit();

                        } else {
                            phoneCaseVariation = variantList.get(0);
                        }
                        variants.add(phoneCaseVariation);
                    }

                    for (PhoneCaseVariation phoneCaseVariation: variants) {
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
                } catch (WebDriverException e) {
                    // Handle WebDriver exception, e.g., log the error
                    System.err.println(e.getMessage());
                    // Navigate back to the search results page
                    continue;
                }

                // Navigate back to the search results page
                driver.navigate().back();
            }
        }

        session.close();

        // Close the browser
        driver.quit();
        System.out.println("✔ AmazonScraper thread finished scraping.");
    }


    private static String[] getModels(String productModels) {
        String[] models = productModels.split("[,/]");
        // Trim each model
        for (int i = 0; i < models.length; i++) {
            models[i] = models[i].trim();
        }
        return models;
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
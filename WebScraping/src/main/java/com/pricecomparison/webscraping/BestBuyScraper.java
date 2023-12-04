package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.CurrencyConverter;
import com.pricecomparison.util.SaveModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class BestBuyScraper extends Thread {
    private final WebDriver driver;
    private final SessionFactory sessionFactory;
    private static final String WEBSITE = "BestBuy";

    // Constructor to inject SessionFactory
    public BestBuyScraper(SessionFactory sessionFactory) {
        this.driver = new ChromeDriver();
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        // Initialize session
        Session session = sessionFactory.openSession();

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

                    // Click on productSpecifications button
                    WebElement specificationsButton = driver.findElement(By.cssSelector("button.specifications-drawer-btn[data-testid]"));
                    if (specificationsButton != null) {
                        specificationsButton.click();

                        try { Thread.sleep((long) (Math.random() * 1000 + 500)); }
                        catch (InterruptedException e) { System.out.println(e.getMessage()); }

                        try {
                            productModels = driver.findElement(By.cssSelector("ul:nth-child(1) div:nth-child(3) div.description")).getText();
                            productColour = driver.findElement(By.cssSelector("ul:nth-child(2) div:nth-child(8) div.description")).getText();
                        } catch (org.openqa.selenium.NoSuchElementException e) {
                            productModels = "N/A";
                            productColour = extractColor(productName);
                        }
                    }

                    String[] models = SaveModel.getModels(productModels);
                    ArrayList<PhoneCase> cases = new ArrayList<>();
                    for (String model : models) {
                        model = SaveModel.filtered(model);

                        if (!SaveModel.isFilteredAndChecked(model)) {
                            continue;
                        }
                        // Create PhoneCase object and save it to the database
                        SaveModel.save(session, cases, model);
                    }

                    ArrayList<PhoneCaseVariation> variants = new ArrayList<>();
                    for (PhoneCase phoneCase : cases) {
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

                    for (PhoneCaseVariation phoneCaseVariation : variants) {
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
                        priceComparison.setPrice(productPrice);
                        priceComparison.setUrl(productInPageHref);

                        session.beginTransaction();
                        session.persist(priceComparison);
                        session.getTransaction().commit();
                    }

                } catch (WebDriverException e) {
                    System.err.println(e.getMessage());
                    continue;
                }

                driver.navigate().back();
            }
        }

        session.close();

        // Close the browser
        driver.quit();
        System.out.println("✔ BestBuyScraper Thread finished scraping.");
    }

    private String extractColor(String productName) {
        //get the color from ProductName after the last dash (-) if there is no dash, return the string "N/A"
        return productName.contains("-") ? productName.substring(productName.lastIndexOf("-") + 1).trim() : "N/A";
    }
}

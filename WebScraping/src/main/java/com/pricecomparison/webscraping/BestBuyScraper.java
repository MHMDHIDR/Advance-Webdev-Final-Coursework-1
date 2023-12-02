package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.CurrencyConverter;
import com.pricecomparison.util.ExtractProductModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BestBuyScraper extends Thread {
    private final SessionFactory sessionFactory;
    private static final String WEBSITE = "BestBuy";

    // Constructor to inject SessionFactory
    public BestBuyScraper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        // Initialize Hibernate session
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            for (int page = 1; page <= Const.MAX_PAGES; page++) {
                String bestBuyUrl = "https://www.bestbuy.com/site/searchpage.jsp?st=iPhone+case&intl=nosplash&cp=" + page;

                // Connect to the Best Buy URL and parse the HTML content
                Document doc = Jsoup.connect(bestBuyUrl).get();

                // Find and process each product on the page
                Elements productElements = doc.select(".sku-item");

                for (Element product : productElements) {
                    String productName = product.select("h4.sku-title a").text();
                    String productLink = "https://www.bestbuy.com" + product.select("h4.sku-title a").attr("href");
                    String productPriceUSD = product.select(".priceView-customer-price span[aria-hidden='true']").text();
                    String convertedPriceGBP = CurrencyConverter.convertToGBP(productPriceUSD).replaceAll("[^\\d.]", "");
                    String productImageURL = product.select("img.product-image").attr("src");
                    String findProductColor = product.select("div.variation-info div div.product-variation-header div.hover-name").text();
                    String productColor = findProductColor.isEmpty() ? extractColor(productName) : findProductColor;

                    // Check if any of the essential data is missing
                    if (productName.isEmpty() || productPriceUSD.isEmpty()) {
                        continue; // Skip this product
                    }

                    // Extract phone model from the product name
                    String phoneModel = ExtractProductModel.model(productName);

                    // Create PhoneCase object and save it to the database
                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setPhoneModel(phoneModel);
                    session.persist(phoneCase);

                    // Create and save PhoneCaseVariation entity
                    PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();
                    phoneCaseVariation.setPhoneCase(phoneCase);
                    phoneCaseVariation.setColor(productColor);
                    phoneCaseVariation.setImageUrl(productImageURL);
                    session.persist(phoneCaseVariation);

                    // Create and save PriceComparison entity with converted price to GBP
                    PriceComparison priceComparison = new PriceComparison();
                    priceComparison.setCaseVariant(phoneCaseVariation);
                    priceComparison.setName(productName);
                    priceComparison.setPrice(convertedPriceGBP);
                    priceComparison.setUrl(productLink);
                    session.persist(priceComparison);

                    // Set PriceComparison in PhoneCaseVariation
                    phoneCaseVariation.setPriceComparison(priceComparison);
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("BestBuy Thread was interrupted: " + e.getMessage());
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }

        System.out.println("✔ BestBuyScraper Thread finished scraping.");
    }

    private String extractColor(String productName) {
        //get the color from ProductName after the last dash (-) if there is no dash, return the string "N/A"
        if (productName.contains("-")) {
            return productName.substring(productName.lastIndexOf("-") + 1).trim();
        } else {
            return "N/A";
        }
    }
}

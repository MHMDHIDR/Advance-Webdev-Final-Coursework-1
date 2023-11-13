package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.CurrencyConverter;
import com.pricecomparison.util.ExtractProductModel;
import com.pricecomparison.util.HibernateUtil;

import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BestBuyScraper extends Thread {
    private static final int MAX_PAGES = 7;

    @Override
    public void run() {
        // Initialize Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            session.beginTransaction();

            for (int page = 1; page <= MAX_PAGES; page++) {
                String bestBuyUrl = "https://www.bestbuy.com/site/searchpage.jsp?st=iPhone+case&intl=nosplash&cp=" + page;

                // Connect to the Best Buy URL and parse the HTML content
                Document doc = Jsoup.connect(bestBuyUrl).get();

                // Find and process each product on the page
                Elements productElements = doc.select(".sku-item");

                for (Element product : productElements) {
                    String productName = product.select("h4.sku-title a").text();
                    String productLink = "https://www.bestbuy.com" + product.select("h4.sku-title a").attr("href");
                    String productPriceUSD = product.select(".priceView-customer-price span[aria-hidden='true']").text();
                    String convertedPriceGBP = CurrencyConverter.convertToGBP(productPriceUSD);
                    String productImageURL = product.select("img.product-image").attr("src");
                    String productColor = product.select("div.variation-info div div.product-variation-header div.hover-name").text();

                    // Check if any of the essential data is missing
                    if (productName.isEmpty() || productPriceUSD.isEmpty()) {
                        continue; // Skip this product
                    }

                    // Extract phone model from the product name
                    String phoneModel = ExtractProductModel.model(productName);

                    // Create PhoneCase object and save it to the database
                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setWebsite("BestBuy");
                    phoneCase.setPhoneModel(phoneModel);
                    session.save(phoneCase);

                    // Create and save PhoneCaseVariation entity
                    PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();
                    phoneCaseVariation.setPhoneCase(phoneCase);
                    phoneCaseVariation.setColor(productColor.isEmpty() ? "DefaultColor" : productColor);
                    phoneCaseVariation.setImageUrl(productImageURL);
                    session.save(phoneCaseVariation);

                    // Create and save PriceComparison entity with converted price to GBP
                    PriceComparison priceComparison = new PriceComparison();
                    priceComparison.setCaseVariant(phoneCaseVariation);
                    priceComparison.setPrice(convertedPriceGBP.replaceAll("[^\\d.]", ""));
                    priceComparison.setUrl(productLink);
                    session.save(priceComparison);

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
}
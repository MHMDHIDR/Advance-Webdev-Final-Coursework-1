package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.DatabaseUtil;
import com.pricecomparison.util.ExtractProductModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class eBayScraper extends Thread {
    private static final int MAX_PAGES = 5;
    private final SessionFactory sessionFactory;

    // Constructor to inject SessionFactory
    public eBayScraper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        // Initialize Hibernate session outside the run method
        Session session = sessionFactory.openSession();

        try {
            for (int page = 1; page <= MAX_PAGES; page++) {
                String ebayUrl = "https://www.ebay.co.uk/sch/i.html?_nkw=iPhone+case&_pgn=" + page;

                // Connect to the eBay URL and parse the HTML content
                Document doc = Jsoup.connect(ebayUrl).get();

                session.beginTransaction();

                // Find and process each product on the page
                Elements productElements = doc.select(".s-item");

                for (Element product : productElements) {
                    String productName = product.select(".s-item__title").text();
                    String productLink = product.select(".s-item__link").attr("href");
                    String productPrice = product.select(".s-item__price").text();
                    Element imageElement = product.select(".s-item__image-wrapper img").first();
                    String productImageURL = imageElement != null ? imageElement.attr("src") : "";

                    // Check if any of the essential data is missing
                    if (productName.isEmpty() || productLink.isEmpty() || productPrice.isEmpty()) {
                        continue; // Skip this product
                    }

                    // Check if data exists in the database
                    if (DatabaseUtil.isDataExists(session, "SELECT COUNT(*) FROM PriceComparison WHERE url = :URL", "URL", productLink)) {
                        System.out.println("Data already exists for URL: " + productLink);
                        continue;
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
                    phoneCaseVariation.setColor(("Click \"View Details\" to view color options"));
                    phoneCaseVariation.setImageUrl(productImageURL);
                    session.persist(phoneCaseVariation);

                    // Create and save PriceComparison entity with converted price to GBP
                    PriceComparison priceComparison = new PriceComparison();
                    priceComparison.setCaseVariant(phoneCaseVariation);
                    priceComparison.setPrice(productPrice);
                    priceComparison.setUrl(productLink);
                    session.persist(priceComparison);

                    // Set PriceComparison in PhoneCaseVariation
                    phoneCaseVariation.setPriceComparison(priceComparison);
                }
                session.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("eBay Thread was interrupted: " + e.getMessage());
        } finally {
            session.close(); // Close the session to release resources
        }
        System.out.println("✔ eBayScraper Thread finished scraping.");
    }
}
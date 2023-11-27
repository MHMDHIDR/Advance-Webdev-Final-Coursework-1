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

public class ArgosScraper extends Thread {
    private static final int MAX_PAGES = 5;
    private final SessionFactory sessionFactory;

    // Constructor to inject SessionFactory
    public ArgosScraper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void run() {
        Session session = sessionFactory.openSession();

        try {
            for (int page = 1; page <= MAX_PAGES; page++) {
                String argosUrl = "https://www.argos.co.uk/search/iphone-case/opt/page:" + page;

                Document doc = Jsoup.connect(argosUrl).get();
                session.beginTransaction();

                // Find and process each product on the page
                Elements productElements = doc.select("div[data-test='component-product-card']");

                for (Element product : productElements) {
                    String productName = product.select("div[data-test='component-product-card-title']").text();
                    String productPrice = product.select("div[data-test='component-product-card-price'] strong").text();
                    String productLink = product.select("[data-test='component-product-card-textContainer'] a[data-test='component-product-card-title-link']").attr("href");
                    String productImageURL = product.select("[data-test='component-product-card']").attr("data-product-id");

                    // Check if any of the essential data is missing
                    if (productName.isEmpty() || productPrice.isEmpty()) {
                        continue; // Skip this product
                    }

                    // Extract color from the product name after the last dash ("-") symbol
                    int lastDashIndex = productName.lastIndexOf("-");
                    // If there is no dash, set color to "DefaultColor"
                    String color = (lastDashIndex != -1) ? productName.substring(lastDashIndex + 1).trim() : "DefaultColor";

                    String productModel = ExtractProductModel.model(productName);

                    // Check if data exists in the database
                    if (DatabaseUtil.isDataExists(session, "SELECT COUNT(*) FROM PriceComparison WHERE url = :URL", "URL", "https://www.argos.co.uk" + productLink)) {
                        System.out.println("Data already exists for URL: " + "https://www.argos.co.uk" + productLink);
                        continue;
                    }

                    // Create PhoneCase object and save it to the database
                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setPhoneModel(productModel);
                    session.persist(phoneCase);

                    // Create and save PhoneCaseVariation entity
                    PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();
                    phoneCaseVariation.setPhoneCase(phoneCase);
                    phoneCaseVariation.setColor(color);
                    phoneCaseVariation.setImageUrl("https://media.4rgos.it/s/Argos/" + productImageURL + "_R_SET");
                    session.persist(phoneCaseVariation);

                    // Create and save PriceComparison entity
                    PriceComparison priceComparison = new PriceComparison();
                    priceComparison.setCaseVariant(phoneCaseVariation);
                    priceComparison.setName(productName);
                    priceComparison.setPrice(productPrice.substring(1)); // Remove the '£' symbol
                    priceComparison.setUrl("https://www.argos.co.uk" + productLink);
                    session.persist(priceComparison);

                    // Set PriceComparison in PhoneCaseVariation
                    phoneCaseVariation.setPriceComparison(priceComparison);
                }
                session.getTransaction().commit(); // Commit the transaction
            }
        } catch (Exception e) {
            System.out.println("Error scraping Argos => " + e.getMessage());
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        System.out.println("✔ ArgosScraper thread finished scraping.");
    }
}

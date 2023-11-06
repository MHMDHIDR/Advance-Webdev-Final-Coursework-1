package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.util.HibernateUtil;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class eBayScraper extends Thread {
    @Override
    public void run() {
        int MAX_PAGES = 3;

        // Initialize Hibernate session
        Session session = HibernateUtil.getSessionFactory().openSession();

        for (int page = 1; page <= MAX_PAGES; page++) {
            String ebayUrl = "https://www.ebay.co.uk/sch/i.html?_nkw=iPhone+case&_pgn=" + page;

            try {
                // Connect to the eBay URL and parse the HTML content
                Document doc = Jsoup.connect(ebayUrl).get();

                session.beginTransaction();

                // Find and process each product on the page
                Elements productElements = doc.select(".s-item");

                for (int i = 0; i < productElements.size(); i++) {
                    // Skip the first element with class ".s-item" because it's not a product just an ad for eBay
                    if (i == 0) {
                        continue;
                    }

                    Element product = productElements.get(i);

                    String productName = product.select(".s-item__title").text();
                    String productLink = product.select(".s-item__link").attr("href");
                    String productPrice = product.select(".s-item__price").text();
                    Element imageElement = product.select(".s-item__image-wrapper img").first();
                    String productImageURL = imageElement.attr("src");

                    // Check if any of the essential data is missing
                    if (productName.isEmpty() || productLink.isEmpty() || productPrice.isEmpty()) {
                        continue; // Skip this product
                    }

                    // Create PhoneCase object and save it to the database
                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setName(productName);
                    phoneCase.setPrice(productPrice);
                    phoneCase.setDescription(productName);
                    phoneCase.setWebsiteLink(productLink);
                    phoneCase.setProductImageUrl(productImageURL);

                    session.save(phoneCase); // Save the PhoneCase object to the database
                }
            } catch (Exception e) {
                System.out.println("eBay Thread was interrupted: " + e.getMessage());
            } finally {
                session.getTransaction().commit(); // Commit the transaction
                session.close();
            }
        }
        System.out.println("✔ eBayScraper Thread finished scraping.");
    }
}

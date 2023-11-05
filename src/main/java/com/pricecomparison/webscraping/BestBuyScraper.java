package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.util.HibernateUtil;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BestBuyScraper extends Thread {
    @Override
    public void run() {
        int MAX_PAGES = 5;

        for (int page = 1; page <= MAX_PAGES; page++) {
            String bestBuyUrl = "https://www.bestbuy.com/site/searchpage.jsp?st=iPhone+case&intl=nosplash&cp=" + page;

            try {
                // Connect to the Best Buy URL and parse the HTML content
                Document doc = Jsoup.connect(bestBuyUrl).get();

                // Initialize Hibernate session
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();

                // Find and process each product on the page
                Elements productElements = doc.select(".sku-item");

                for (Element product : productElements) {
                    String productName = product.select("h4.sku-title a").text();
                    String productLink = "https://www.bestbuy.com" + product.select("h4.sku-title a").attr("href");
                    String productPrice = product.select(".priceView-hero-price span").text();
                    String productImageURL = product.select("img.product-image").attr("src");

                    // Check if any of the essential data is missing
                    if (productName.isEmpty() || productPrice.isEmpty()) {
                        continue; // Skip this product
                    }

                    // Create PhoneCase object and save it to the database
                    PhoneCase phoneCase = new PhoneCase();
                    phoneCase.setName(productName);
                    phoneCase.setPrice(productPrice);
                    phoneCase.setDescription(productName);
                    phoneCase.setWebsiteLink(productLink);
                    phoneCase.setProductImageUrl(productImageURL);

                    session.save(phoneCase);
                }

                session.getTransaction().commit(); // Commit the transaction
                session.close();
            } catch (Exception e) {
                System.out.println("Thread was interrupted: " + e.getMessage());
            }
        }
    }
}

package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.ExtractProductModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class ArgosScraper extends WebScrapper {
    private static final String WEBSITE = "Argos";

    @Override
    public void run() {
        // if <= Const.MAX_PAGES is less than or equals to 6 give me Const.MAX_PAGES
        // otherwise give me 6 argos scraper only has 6 pages
        for (int page = 1; page <= (Math.min(Const.MAX_PAGES, 6)); page++) {
            String argosUrl = "https://www.argos.co.uk/search/iphone-case/opt/page:" + page;
            Document doc = null;

            try {
                doc = Jsoup.connect(argosUrl).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Find and process each product on the page
            Elements productElements = doc.select("div[data-test='component-product-card']");

            for (Element product : productElements) {
                String productName = product.select("div[data-test='component-product-card-title']").text();
                String productPrice = product.select("div[data-test='component-product-card-price'] strong").text().substring(1);// Remove the pound symbol from the price
                String productLink = product.select("[data-test='component-product-card-textContainer'] a[data-test='component-product-card-title-link']").attr("href");
                String productImageURL = product.select("[data-test='component-product-card']").attr("data-product-id");

                // Extract color from the product name after the last dash ("-") symbol
                int lastDashIndex = productName.lastIndexOf("-");
                // If there is no dash, set color to "DefaultColor"
                String productColour = (lastDashIndex != -1) ? productName.substring(lastDashIndex + 1).trim() : "clear";

                String[] models = ExtractProductModel.model(productName).replace("Apple", "").trim().split(",");
                ArrayList<PhoneCase> cases = new ArrayList<>();
                for (String model : models) {
                    model = caseDao.filtered(model);
                    if (!caseDao.isFilteredAndChecked(model)) {
                        continue;
                    }
                    // Create PhoneCase object and save it to the database
                    caseDao.saveCase(cases, model);
                }

                ArrayList<PhoneCaseVariation> variants = new ArrayList<PhoneCaseVariation>();
                for (PhoneCase phoneCase : cases) {
                    // Create PhoneCaseVariation object and save it to the database
                    caseDao.saveVariant(variants, phoneCase, productColour, productImageURL);
                }

                for (PhoneCaseVariation phoneCaseVariation: variants) {
                    // Create PriceComparison object and save it to the database
                    caseDao.savePrice(phoneCaseVariation, WEBSITE, productName, productPrice, productLink);
                }
            }
        }

        System.out.println("✔ ArgosScraper thread finished scraping.");
    }
}

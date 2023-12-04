package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import com.pricecomparison.util.Const;
import com.pricecomparison.util.ExtractProductModel;
import com.pricecomparison.util.SaveModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class ArgosScraper extends Thread {

    private SaveModel saveModel;
    private WebDriver driver;
    private SessionFactory sessionFactory;
    private static final String WEBSITE = "Argos";

    // Constructor to inject SessionFactory
    public ArgosScraper() {
        this.driver = new ChromeDriver();
    }

    @Override
    public void run() {
        // Initialize session
        Session session = sessionFactory.openSession();
        SaveModel saveModel = new SaveModel();

        try {
            // if <= Const.MAX_PAGES is less than or equals to 6 give me Const.MAX_PAGES
            // otherwise give me 6 argos scraper only has 6 pages
            for (int page = 1; page <= (Math.min(Const.MAX_PAGES, 6)); page++) {
                String argosUrl = "https://www.argos.co.uk/search/iphone-case/opt/page:" + page;

                Document doc = Jsoup.connect(argosUrl).get();

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
                        model = SaveModel.filtered(model);

                        if (!SaveModel.isFilteredAndChecked(model)) {
                            continue;
                        }
                        // Create PhoneCase object and save it to the database
                        saveModel.save(cases, model);
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
                            phoneCaseVariation.setImageUrl("https://media.4rgos.it/s/Argos/" + productImageURL);

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
                        priceComparison.setPrice(productPrice);
                        priceComparison.setUrl("https://www.argos.co.uk" + productLink);

                        session.beginTransaction();
                        session.persist(priceComparison);
                        session.getTransaction().commit();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error scraping Argos => " + e.getMessage());
        }

        session.close();
        System.out.println("✔ ArgosScraper thread finished scraping.");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public SaveModel getSaveModel() {
        return saveModel;
    }

    public void setSaveModel(SaveModel saveModel) {
        this.saveModel = saveModel;
    }
}

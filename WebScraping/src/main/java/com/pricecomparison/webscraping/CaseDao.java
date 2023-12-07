package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.MetadataSources;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class CaseDao {
    private SessionFactory sessionFactory;
    private WebDriver driver;

    public void init() {
        try {
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
            standardServiceRegistryBuilder.configure("resources/hibernate.cfg.xml");

            StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
            try {
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            } catch (Exception e) {
                System.out.println("Error in creating SessionFactory object." + e.getMessage());
                e.printStackTrace();
                StandardServiceRegistryBuilder.destroy(registry);
            }
        } catch (Throwable e) {
            System.out.println("Error in creating SessionFactory object." + e.getMessage());
        }
    }

    public void saveCase(ArrayList<PhoneCase> cases, String model) {
        Session session = sessionFactory.openSession();
        String filteredModel = filtered(model).toLowerCase();

        List<PhoneCase> caseList = session.createQuery("FROM PhoneCase WHERE phoneModel = :MODEL", PhoneCase.class)
                .setParameter("MODEL", filteredModel)
                .getResultList();
        PhoneCase phoneCase = new PhoneCase();
        if (caseList.isEmpty()) {
            phoneCase.setPhoneModel(filteredModel);
            session.beginTransaction();
            session.persist(phoneCase);
            session.getTransaction().commit();
        } else {
            phoneCase = caseList.get(0);
        }
        cases.add(phoneCase);

        session.close();
    }

    public void saveVariant(ArrayList<PhoneCaseVariation> variants, PhoneCase phoneCase, String color, String imgUrl) {
        Session session = sessionFactory.openSession();

        List<PhoneCaseVariation> variantList = session.createQuery("FROM PhoneCaseVariation WHERE phoneCase = :MODEL AND color = :COLOR AND imageUrl = :IMAGE_URL", PhoneCaseVariation.class)
                .setParameter("MODEL", phoneCase)
                .setParameter("COLOR", color)
                .setParameter("IMAGE_URL", imgUrl)
                .getResultList();
        PhoneCaseVariation phoneCaseVariation = new PhoneCaseVariation();
        if (variantList.isEmpty()) {
            phoneCaseVariation.setPhoneCase(phoneCase);
            phoneCaseVariation.setColor(color);
            phoneCaseVariation.setImageUrl(imgUrl);

            session.beginTransaction();
            session.persist(phoneCaseVariation);
            session.getTransaction().commit();
        } else {
            phoneCaseVariation = variantList.get(0);
        }
        variants.add(phoneCaseVariation);

        session.close();
    }

    public void savePrice(PhoneCaseVariation phoneCaseVariation, String Website, String productName, String productPrice, String productUrl) {
        Session session = sessionFactory.openSession();

        List<PriceComparison> priceComparisons = session.createQuery("FROM PriceComparison WHERE caseVariant = :MODEL AND website = :WEBSITE", PriceComparison.class)
                .setParameter("MODEL", phoneCaseVariation)
                .setParameter("WEBSITE", Website)
                .getResultList();
        PriceComparison priceComparison = new PriceComparison();
        if (!priceComparisons.isEmpty()) {
            priceComparison = priceComparisons.get(0);
        }
        priceComparison.setCaseVariant(phoneCaseVariation);
        priceComparison.setName(productName);
        priceComparison.setWebsite(Website);
        priceComparison.setPrice(productPrice);
        priceComparison.setUrl(productUrl);

        try {
            session.beginTransaction();
            session.persist(priceComparison); //try to use merge
            session.getTransaction().commit();
        }
        // org.hibernate.exception.DataException is thrown when the
        // url is too long to be stored in the database
        catch (org.hibernate.exception.DataException e) {
            System.err.println(e.getMessage());
            session.getTransaction().rollback(); //rollback the transaction
        }

        session.close();
    }

    //filter models from unwanted characters
    public String filtered(String filteredModel) {
        return filteredModel
                .replaceAll("\\[|For Apple|For|Apple|]", "")
                .replaceAll("\\([^)]*\\)", "")
                .replaceAll("6\\.1''|6\\.1\"|5\\.4''|5\\.4\"", "")
                .replaceAll("\\s+", " ") // Replace multiple spaces with a single space
                .trim();
    }

    //check if model is filtered and checked
    public boolean isFilteredAndChecked(String cleanedModel) {
        cleanedModel = cleanedModel.toLowerCase();
        return cleanedModel.startsWith("iphone ")
                && !cleanedModel.contains("-")
                && !cleanedModel.contains("...")
                //this should match iPhone [number], iPhone [number]s, iPhone [number]c, iPhone [number] Pro, iPhone [number] Plus
                // iPhone [number] Pro Max, iPhone [number] Mini, iPhone x, iPhone xr, iPhone xs, iPhone xs max, iPhone se
                && cleanedModel.matches("(?i)iPhone\\s\\d+|iPhone\\s\\d+s|iPhone\\s\\d+c|iPhone\\s\\d+\\sPro|iPhone\\s\\d+\\sPlus|iPhone\\s\\d+\\sPro\\sMax|iPhone\\s\\d+\\sMini|iPhone\\sx|iPhone\\sxr|iPhone\\sxs|iPhone\\sxs\\smax|iPhone\\sse");
    }

    public String[] getModels(String productModels) {
        String[] models = productModels.split("[,/]");
        // Trim each model
        for (int i = 0; i < models.length; i++) {
            models[i] = models[i].trim();
        }
        return models;
    }

    public void printData(String productUrl, String productName, String productPrice, String productImageURL, String productModels, String productColour) {
        System.out.println("Product URL: " + productUrl + "\n" +
                "Product Name: " + productName + "\n" +
                "productPrice: " + productPrice + "\n" +
                "productImageURL: " + productImageURL + "\n" +
                "productModels: " + productModels + "\n" +
                "productColour: " + productColour + "\n" +
        "-------------------------------------------------------------");
    }
}

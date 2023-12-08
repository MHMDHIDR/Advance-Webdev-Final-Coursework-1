package com.pricecomparison.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.PhoneCaseVariation;
import com.pricecomparison.PriceComparison;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.MetadataSources;

import java.util.ArrayList;
import java.util.List;

public class CaseDao {
    private SessionFactory sessionFactory;

    /**
     * Initialize the SessionFactory instance.
     */
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

    /**
     * saveCase saves the phone case model to the cases_variant table in the pricecomparison_db databases.
     * @param cases ArrayList of PhoneCase objects to be saved to the database
     * @param model String of the phone model
     * uses my custom method filtered() to remove unwanted characters from the model
     */
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

    /**
     * saveVariant saves the phone case variant to the cases_variant table in the pricecomparison_db database.
     * @param variants ArrayList of PhoneCaseVariation objects to be saved to the database
     * @param phoneCase PhoneCase object to be saved to the database
     * @param color String of the phone case color
     * @param imgUrl String of the phone case image url
     */
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

    /**
     * savePrice saves the phone case price to the price_comparison table in the pricecomparison_db database.
     * @param phoneCaseVariation PhoneCaseVariation object to be saved to the database
     * @param Website String of the website name
     * @param productName String of the product name
     * @param productPrice String of the product price
     * @param productUrl String of the product url
     */
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

    /**
     * filtered() removes unwanted characters from the model
     * @param filteredModel String of the model to be filtered
     * uses regex to remove unwanted characters
     * @return String of the filtered model
     */
    public String filtered(String filteredModel) {
        return filteredModel
                .replaceAll("\\[|For Apple|For|Apple|]", "")
                .replaceAll("\\([^)]*\\)", "")
                .replaceAll("6\\.1''|6\\.1\"|5\\.4''|5\\.4\"", "")
                .replaceAll("\\s+", " ") // Replace multiple spaces with a single space
                .trim();
    }

    /**
     * isFilteredAndChecked() checks if the model is filtered and checked
     * @param cleanedModel String of the model to be checked
     * uses regex to check if the model is filtered and checked
     * @return boolean true if the model is filtered and checked, false otherwise
     */
    public boolean isFilteredAndChecked(String cleanedModel) {
        cleanedModel = cleanedModel.toLowerCase();
        return cleanedModel.startsWith("iphone ")
                && !cleanedModel.contains("-")
                && !cleanedModel.contains("...")
                //this should match iPhone [number], iPhone [number]s, iPhone [number]c, iPhone [number] Pro, iPhone [number] Plus
                // iPhone [number] Pro Max, iPhone [number] Mini, iPhone x, iPhone xr, iPhone xs, iPhone xs max, iPhone se
                && cleanedModel.matches("(?i)iPhone\\s\\d+|iPhone\\s\\d+s|iPhone\\s\\d+c|iPhone\\s\\d+\\sPro|iPhone\\s\\d+\\sPlus|iPhone\\s\\d+\\sPro\\sMax|iPhone\\s\\d+\\sMini|iPhone\\sx|iPhone\\sxr|iPhone\\sxs|iPhone\\sxs\\smax|iPhone\\sse");
    }

    /**
     * getModels() splits the productModels string into an array of models
     * @param productModels String of the product models
     * uses regex to split the productModels string
     * @return String[] of the models
     */
    public String[] getModels(String productModels) {
        String[] models = productModels.split("[,/]");
        // Trim each model
        for (int i = 0; i < models.length; i++) {
            models[i] = models[i].trim();
        }
        return models;
    }

    /**
     * printData() prints the product information below:
     * @param productUrl String of the product url
     * @param productName String of the product name
     * @param productPrice String of the product price
     * @param productImageURL String of the product image url
     * @param productModels String of the product models
     * @param productColour String of the product colour
     */
    public void printData(String productUrl, String productName, String productPrice, String productImageURL, String productModels, String productColour) {
        System.out.println(
            "Product URL: " + productUrl + "\n" +
            "Product Name: " + productName + "\n" +
            "productPrice: " + productPrice + "\n" +
            "productImageURL: " + productImageURL + "\n" +
            "productModels: " + productModels + "\n" +
            "productColour: " + productColour + "\n" +
        "-------------------------------------------------------------");
    }
}

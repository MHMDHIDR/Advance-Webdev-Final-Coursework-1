package com.pricecomparison.util;

import com.pricecomparison.PhoneCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class SaveModel {
    SessionFactory sessionFactory;

    public void save(ArrayList<PhoneCase> cases, String model) {
        Session session = sessionFactory.getCurrentSession();
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

    //filter models from unwanted characters
    public static String filtered(String filteredModel) {
        return filteredModel
                .replaceAll("\\[|For Apple|For|Apple|]", "")
                .replaceAll("\\([^)]*\\)", "")
                .replaceAll("6\\.1''|6\\.1\"|5\\.4''|5\\.4\"", "")
                .replaceAll("\\s+", " ") // Replace multiple spaces with a single space
                .trim();
    }

    //check if model is filtered and checked
    public static boolean isFilteredAndChecked(String cleanedModel) {
        cleanedModel = cleanedModel.toLowerCase();
        return cleanedModel.startsWith("iphone ")
        && !cleanedModel.contains("-")
        && !cleanedModel.contains("...")
        && cleanedModel.matches("(?i)iPhone\\s\\d+|iPhone\\s\\d+s|iPhone\\s\\d+c|iPhone\\s\\d+\\sPro|iPhone\\s\\d+\\sPlus|iPhone\\s\\d+\\sPro\\sMax|iPhone\\s\\d+\\sMini|iPhone\\sx|iPhone\\sxr|iPhone\\sxs|iPhone\\sxs\\smax");
    }

    public static String[] getModels(String productModels) {
        String[] models = productModels.split("[,/]");
        // Trim each model
        for (int i = 0; i < models.length; i++) {
            models[i] = models[i].trim();
        }
        return models;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

package com.pricecomparison.util;

import org.hibernate.Session;

public class DatabaseUtil {
    // Utility method to check if data exists in the database
    public static boolean isDataExists(Session session, String query, String parameterName, Object parameterValue) {
        Long count = (Long) session.createSelectionQuery(query)
                .setParameter(parameterName, parameterValue)
                .uniqueResult();

        return count != null && count > 0;
    }
}

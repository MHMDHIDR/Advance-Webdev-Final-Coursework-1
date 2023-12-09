package com.pricecomparison.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>Extracts the iPhone model(s) class</h1>
 * <p>
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class ExtractProductModel {
    /**
     * Extracts the iPhone model(s) from the product name
     * @param productName - the name of the product
     * matches it with the regex pattern
     * and then returns the matched model(s)
     */
    public static String model(String productName) {
        // Use a regex pattern to match the iPhone model(s) in the product name
        Pattern pattern = Pattern.compile("(iPhone\\s*\\d+\\s*(?:Pro\\s*Max|Pro|Mini)?(?:\\s*[XRS]+(?:\\s*Pro\\s*Max|\\s*Pro|\\s*Mini)?)?(?:\\s*S)?)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(productName);

        StringBuilder models = new StringBuilder();

        // Find all matches in the product name
        while (matcher.find()) {
            if (models.length() > 0) {
                models.append(", ");
            }

            String matchedModel = matcher.group(1).trim();
            models.append(matchedModel);

            if (matcher.end() < productName.length() && !Character.isLetterOrDigit(productName.charAt(matcher.end()))) {
                models.append(" ");
            }
        }

        // If no matches are found, return the original name
        return models.length() > 0 ? models.toString().toLowerCase() : productName.trim().toLowerCase();
    }
}

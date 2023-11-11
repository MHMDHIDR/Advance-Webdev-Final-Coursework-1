package com.pricecomparison.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractProductModel {
    public static String model(String productName) {
        // Use a regex pattern to match the iPhone model(s) in the product name
        Pattern pattern = Pattern.compile("iPhone\\s(\\d+(?:\\s*Pro\\s*Max|\\s*Pro|\\s*Mini)?(?:\\s*(?:and)?\\s*(?:[\\dXRS]+(?:\\s*Pro\\s*Max|\\s*Pro|\\s*Mini)?)?)?(?:\\s*S)?)");
        Matcher matcher = pattern.matcher(productName);

        StringBuilder models = new StringBuilder();

        // Find all matches in the product name
        while (matcher.find()) {
            if (models.length() > 0) {
                models.append(", ");
            }

            models.append(matcher.group(1).trim());

            if (matcher.end() < productName.length() && !Character.isLetterOrDigit(productName.charAt(matcher.end()))) {
                models.append(" ");
            }
        }

        // If no matches are found, return the original name
        return models.length() > 0 ? models.toString() : productName.trim();
    }
}
package com.pricecomparison.util;

public class CurrencyConverter {
    public static String convertToGBP(String productPriceUSD) {
        // Extract the numeric part of the price
        String numericPart = extractNumericPart(productPriceUSD);

        // Convert to GBP (assuming a simple conversion for demonstration purposes)
        double gbpRate = 0.81; // Replace with the actual conversion rate
        double convertedPrice = Double.parseDouble(numericPart) * gbpRate;

        return "£" + String.format("%.2f", convertedPrice);
    }

    private static String extractNumericPart(String input) {
        // Remove non-numeric characters except for the last dot
        input = input.replaceAll("[^\\d.]+", "");

        // Handle cases with multiple dots by keeping only the last one
        int lastDotIndex = input.lastIndexOf('.');
        if (lastDotIndex != -1) {
            input = input.substring(0, lastDotIndex) + input.substring(lastDotIndex).replace(".", "");
        }

        return input;
    }
}

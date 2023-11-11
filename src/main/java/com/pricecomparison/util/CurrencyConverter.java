package com.pricecomparison.util;

public class CurrencyConverter {
    public static String convertToGBP(String productPriceUSD) {
        // Extract the numeric part of the price
        String numericPart = productPriceUSD.replaceAll("[^\\d.]+", "");

        // Convert to GBP (assuming a simple conversion for demonstration purposes)
        double gbpRate = 0.81; // Replace with the actual conversion rate
        double convertedPrice = Double.parseDouble(numericPart) * gbpRate;

        return "£" + String.format("%.2f", convertedPrice);
    }
}

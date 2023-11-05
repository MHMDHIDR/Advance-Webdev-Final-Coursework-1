package com.pricecomparison.util;

public class CurrencyConverter {
    public static String convertToGBP(String productPriceUSD) {
        String[] priceParts = productPriceUSD.split(" - ");

        StringBuilder convertedPrice = new StringBuilder();
        for (int i = 0; i < priceParts.length; i++) {
            double usdValue = Double.parseDouble(priceParts[i].replaceAll("[^0-9.]+", ""));
            double gbpValue = usdValue * 0.81;
            convertedPrice.append("£").append(String.format("%.2f", gbpValue));

            if (priceParts.length > 1 && i < priceParts.length - 1) {
                convertedPrice.append(" - ");
            }
        }

        return convertedPrice.toString();
    }
}

package com.pricecomparison.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverter {
    public static String convertToGBP(String productPriceUSD) {
        // Convert to GBP
        double gbpRate = 0.79;
        // Extract the numeric part of the price as a double
        double numericPartDouble = Double.parseDouble(productPriceUSD.replaceAll("[^\\d.]+", ""));
        // Multiply the numeric part by the rate
        double convertedPriceGBP = numericPartDouble * gbpRate;
        // Round the converted price to 2 decimal places
        convertedPriceGBP = new BigDecimal(convertedPriceGBP).setScale(2, RoundingMode.HALF_UP).doubleValue();

        return String.valueOf(convertedPriceGBP);
    }

}

package com.pricecomparison.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * <h2>Utility class to convert currency</h2>
 * <br>
 *
 * @see <a href="https://www.baeldung.com/java-round-decimal-number">https://www.baeldung.com/java-round-decimal-number</a>
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class CurrencyConverter {
    /**
     * CurrencyConverter constructor for the utility class
     */
    public CurrencyConverter() {}
    /**
     * Convert a price from USD to GBP
     * @param productPriceUSD The price to convert
     * @return The converted price
     */
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

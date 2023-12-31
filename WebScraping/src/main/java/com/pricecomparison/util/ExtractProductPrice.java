package com.pricecomparison.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * <h2>Utility class to extract the price of a product from a website</h2>
 * <p>
 * args: driver - the WebDriver object
 * returns: the price of the product as a String
 * </p>
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class ExtractProductPrice {
    /**
     * This is the constructor of the ExtractProductPrice class.
     */
    public ExtractProductPrice() {}

    /**
     * @param driver the WebDriver object to be used
     * The method takes a WebDriver object as an argument, and returns a String
     * containing the price of the product.
     * <p>
     * The method uses two different CSS selectors to extract the price, and if the
     * first selector fails, it tries the second selector.
     * If both selectors fail, the method logs an error and returns a default value of £0.00.
     * @return the price of the product as a String
     * throws firstSelectorException Exception if both selectors fail
     */
    public static String price(WebDriver driver) {
        try {
            // Try the first selector
            WebElement priceElement = driver.findElement(By.cssSelector("span[aria-hidden='true'] span.a-price-whole"));
            String wholePart = priceElement.getText().trim();
            WebElement fractionElement = priceElement.findElement(By.xpath("following-sibling::span[@class='a-price-fraction']"));
            String fractionPart = fractionElement.getText().trim();
            return "£" + wholePart + "." + fractionPart;
        } catch (Exception firstSelectorException) {
            try {
                // If the first selector fails, try the alternative selector
                WebElement priceElementAlternative = driver.findElement(By.cssSelector("div.css-1qfzbsu span h3[data-q='ad-price']"));
                return priceElementAlternative.getText().trim();
            } catch (Exception alternativeSelectorException) {
                // If both selectors fail, log an error and return default value
                System.err.println("Error extracting price, default (£0.00)");
                return "£0.00";
            }
        }
    }
}

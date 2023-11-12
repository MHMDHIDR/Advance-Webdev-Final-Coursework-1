package com.pricecomparison.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ExtractProductPrice {
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
                String wholePart = priceElementAlternative.getText().trim();
                return wholePart;
            } catch (Exception alternativeSelectorException) {
                // If both selectors fail, log an error and return default value
                System.err.println("Error extracting price, default (£0.00)");
                return "£0.00";
            }
        }
    }
}

package com.pricecomparison.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ExtractProductPrice {
    public static String price(WebDriver driver) {
        try {
            WebElement priceElement = driver.findElement(By.cssSelector("span[aria-hidden='true'] span.a-price-whole"));
            String wholePart = priceElement.getText().trim();
            WebElement fractionElement = priceElement.findElement(By.xpath("following-sibling::span[@class='a-price-fraction']"));
            String fractionPart = fractionElement.getText().trim();
            return "£" + wholePart + "." + fractionPart;
        } catch (Exception e) {
            System.err.println("Error extracting product price. Setting to default (£0.00).");
            return "£0.00";
        }
    }
}

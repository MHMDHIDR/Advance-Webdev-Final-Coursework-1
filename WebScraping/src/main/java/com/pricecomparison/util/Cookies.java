package com.pricecomparison.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * <h1>Cookies class</h1>
 * The Cookies class implements a method to click on accept cookies
 * button if the button is found, then print a message.
 * <p>
 * <b>Note:</b> This class is used by the WebScrapper class.
 * </p>
 *
 * @author  Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since   2023-12-10
 */
public class Cookies {
    /**
     * Accept cookies is a method to click on accept cookies, then print a message
     * @param driver the WebDriver
     * @param cookiesButtonSelector the css selector of the cookies button to be clicked
     * @param WEBSITE the name of the website
     *
     *    <p>
     *        Example:
     *        <pre>
     *            {@code
     *                acceptCookies(driver,"sp-cc-accept","Amazon");
     *            }
     *        </pre>
     *
     *        Output:
     *         <pre>
     *             {@code
     *                 Amazon Cookies button clicked.
     *             }
     *         </pre>
     *    </p>
     *
     */
    public void accept(WebDriver driver, String cookiesButtonSelector, String WEBSITE) {
        try {
            WebElement cookiesButton = driver.findElement(By.cssSelector(cookiesButtonSelector));
            cookiesButton.click();
            System.out.println(WEBSITE +" Cookies button clicked.");
        } catch (Exception e) {
            System.err.println(WEBSITE +" Cookies Not found. Continuing without clicking it.");
        }
    }
}

package com.pricecomparison.test.webscraping;

import com.pricecomparison.PhoneCase;
import com.pricecomparison.webscraping.CaseDao;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 *     This class uses the JUnit to test caseDao methods.
 * </p>
 *
 * <h1>Example usage:</h1>
 *     <pre>{@code
 *         TestCaseDao testCaseDao = new TestCaseDao();
 *         testCaseDao.setUp();
 *         testCaseDao.testFiltered();
 *         testCaseDao.testIsFilteredAndChecked();
 *         testCaseDao.testSaveCaseAfterIsFilteredAndChecked();
 *         testCaseDao.testPrintData();
 *         testCaseDao.tearDown();
 *     }</pre>
 *
 * @author Mohammed Ibrahim  <a href="https://github.com/MHMDHIDR">Mohammed Ibrahim</a>
 * @version 1.0
 * @since 2023-12-10
 */
public class TestCaseDao {
    private CaseDao caseDao;

    @BeforeEach
    void setUp() {
        caseDao = new CaseDao();
        // Initialize the sessionFactory to use it in the test methods
        caseDao.init();
    }

    @Test
    @DisplayName("Test filtered method")
    void testFiltered() {
        // Mock data for testing
        String model = "[For Apple iPhone 4s]";
        String filteredModel = caseDao.filtered(model);

        // Verify the filtered model
        assertEquals("iPhone 4s", filteredModel);
    }

    @Test
    @DisplayName("Test isFilteredAndChecked method")
    void testIsFilteredAndChecked() {
        // Mock data for testing
        String model = "iPhone 4s";
        boolean isFilteredAndChecked = caseDao.isFilteredAndChecked(model);

        // Verify the filtered and checked model
        assertTrue(isFilteredAndChecked);
    }

    @Test
    @DisplayName("Test saveCase method after isFilteredAndChecked")
    void testSaveCaseAfterIsFilteredAndChecked() {
        // Mock data for testing
        ArrayList<PhoneCase> cases = new ArrayList<>();
        String model = "iPhone 4s";

        // Ensure that isFilteredAndChecked passes for the given model
        assertTrue(caseDao.isFilteredAndChecked(model));

        // Call the saveCase method
        caseDao.saveCase(cases, model);

        // Verify the 'cases' list is not empty
        assertFalse(cases.isEmpty());
        PhoneCase savedPhoneCase = cases.get(0);

        // Add more assertions based on your database structure and the expected behavior of saveCase
        assertNotNull(savedPhoneCase);
        assertEquals(model.toLowerCase(), savedPhoneCase.getPhoneModel());
    }


    @Test
    @DisplayName("Test printData method")
    void testPrintData() {
        // Redirect System.out to capture printed data
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        // Mock data for testing
        String PRODUCT_URL = "https://ultra-noob-company.com";
        String PRODUCT_NAME = "iPhone 14, iPhone 14 Plus Fancy Case From Ultra noob company";
        String PRODUCT_PRICE = "19.99";
        String PRODUCT_IMAGE_URL = "https://example.com/case_image.jpg";
        String PRODUCT_MODELS = "iPhone 14, iPhone 14 Plus";
        String PRODUCT_COLOUR = "Red";

        // Call the printData method
        caseDao.printData(
            PRODUCT_URL,
            PRODUCT_NAME,
            PRODUCT_PRICE,
            PRODUCT_IMAGE_URL,
            PRODUCT_MODELS,
            PRODUCT_COLOUR
        );

        // Reset System.out to its original state
        System.setOut(System.out);

        // Verify the printed data
        String expectedOutput = "Product URL: " + PRODUCT_URL + "\n" +
                "Product Name: " + PRODUCT_NAME + "\n" +
                "productPrice: " + PRODUCT_PRICE + "\n" +
                "productImageURL: " + PRODUCT_IMAGE_URL + "\n" +
                "productModels: " + PRODUCT_MODELS + "\n" +
                "productColour: " + PRODUCT_COLOUR + "\n" +
                "-------------------------------------------------------------\n";

        assertEquals(expectedOutput, outputStreamCaptor.toString());
    }

    @AfterEach
    void tearDown() {
        caseDao = null;
    }
}

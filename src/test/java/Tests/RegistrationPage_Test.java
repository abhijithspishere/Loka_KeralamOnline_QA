package Tests;

import Hooks.Hook;
import PageObjects.RegistrationPage;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import java.io.IOException;
import Utils.ExcelUtils;

public class WelcomePage_test extends Hook {

    @DataProvider(name = "registrationData")
    public Object[][] getRegistrationData() throws IOException {
        String filePath = "src/test/resources/testdata/RegistrationData.xlsx";
        String sheetName = "Sheet1";
        return ExcelUtils.getTestData(filePath, sheetName);
    }

    @Test(
            priority = 1,
            testName = "CLKOI-AT-GCCDW-01_Complete_Registration_Flow",
            description = "Verify complete end-to-end registration flow using OTP",
            dataProvider = "registrationData"

    )
    public void registration_test(
            String yopmailUsername,  // Excel column 1 - yopmail username (e.g., "abhijithloka4532")
            String firstName,        // Excel column 2
            String middleName,       // Excel column 3
            String lastName,         // Excel column 4
            String phoneNumber,      // Excel column 5
            String password          // Excel column 6
    ) {
        int experienceYears = 5;


        logger.info("========== TEST STARTED ==========");
        logger.info("User: " + firstName + " " + lastName);
        logger.info("Yopmail Username: " + yopmailUsername);
        logger.info("Phone: " + phoneNumber);

        test.log(Status.INFO, "Test started");
        test.log(Status.INFO, "User: " + firstName + " " + lastName);
        test.log(Status.INFO, "Yopmail Username: " + yopmailUsername);
        test.log(Status.INFO, "Phone: " + phoneNumber);

        RegistrationPage welcomePage = new RegistrationPage(driver);

        try {
            /* ---------- STEP 1 : Launch & Explore ---------- */
            logger.info("STEP 1: Closing popup and exploring");
            welcomePage.clickCloseButton();
            welcomePage.clickExploreButton();

            /* ---------- STEP 2 : Register using Yopmail ---------- */
            logger.info("STEP 2: Registering with Yopmail: " + yopmailUsername);
            welcomePage.registerWithYopmail(yopmailUsername);

            /* ---------- STEP 3 : Country & City Selection ---------- */
            logger.info("STEP 3: Selecting country and city");
            welcomePage.selectCountryAndCity();

            /* ---------- STEP 4 : Fill Personal Details ---------- */
            logger.info("STEP 4: Filling personal details");
            welcomePage.fillPersonalDetails(
                    firstName,       // From Excel
                    middleName,      // From Excel
                    lastName,        // From Excel
                    phoneNumber,     // From Excel
                    password,        // From Excel
                    experienceYears  // Hardcoded
            );

            /* ---------- STEP 5 : Registration Validation ---------- */
            logger.info("STEP 5: Validating registration");
            boolean isRegistered = welcomePage.isRegistrationSuccessful();

            Assert.assertTrue(
                    isRegistered,
                    "Registration failed for user: " + firstName + " " + lastName
            );

            logger.info("Registration validation successful");
            test.log(Status.PASS, "Registration verified successfully");

            /* ---------- STEP 6 : Logout ---------- */
            logger.info("STEP 6: Logging out registered user");
            welcomePage.logoutRegisteredUser();
            logger.info("User logged out successfully");

            test.log(Status.PASS, "End-to-end registration flow completed successfully");
            logger.info("========== TEST PASSED ==========");


        } catch (Exception e) {
            logger.error("Test failed: ", e);
            test.log(Status.FAIL, "Test failed: " + e.getMessage());
            Assert.fail("Test failed: " + e.getMessage());
        }
}}

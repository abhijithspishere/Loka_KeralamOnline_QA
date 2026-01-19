package Tests;

import Hooks.Hook;
import PageObjects.WelcomePage;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WelcomePage_test extends Hook {

    @Test(
            priority = 1,
            testName = "CLKOI-AT-GCCDW-01_Complete_Registration_Flow",
            description = "Verify complete end-to-end registration flow using Yopmail OTP"
    )
    public void testRegistrationWithYopmail() {

        logger.info("========== TEST STARTED : testRegistrationWithYopmail ==========");
        test.log(Status.INFO, "Test started: Registration using Yopmail OTP");

        WelcomePage welcomePage = new WelcomePage(driver);

        try {
            /* ---------- STEP 1 : Launch & Explore ---------- */
            logger.info("STEP 1: Closing initial popup and clicking Explore");
            test.log(Status.INFO, "Closing welcome popup and exploring application");

            welcomePage.clickCloseButton();
            welcomePage.clickExploreButton();

            /* ---------- STEP 2 : Register using Yopmail ---------- */
            logger.info("STEP 2: Starting registration using Yopmail OTP");
            test.log(Status.INFO, "Registering user using Yopmail OTP");

            welcomePage.registerWithYopmail();

            /* ---------- STEP 3 : Country & City Selection ---------- */
            logger.info("STEP 3: Selecting country and city");
            test.log(Status.INFO, "Selecting country and city details");

            welcomePage.selectCountryAndCity();

            /* ---------- STEP 4 : Fill Personal Details ---------- */
            logger.info("STEP 4: Filling personal details and completing registration");
            test.log(Status.INFO, "Entering personal details and submitting registration");

            welcomePage.fillPersonalDetails(
                    "Abhijith",
                    "S",
                    "Suresh",
                    "2000-10-11",
                    "9876543210",
                    "Software Tester",
                    "Test@123",
                    5
            );

            /* ---------- STEP 5 : Registration Validation ---------- */
            /*logger.info("STEP 5: Validating successful registration via Logout button");
            test.log(Status.INFO, "Validating registration success using Logout visibility");*/

//            boolean isRegistered = welcomePage.isRegistrationSuccessful();

           /* Assert.assertTrue(
                    isRegistered,
                    "Registration failed: Logout button not visible or enabled"
            );*/

           /* logger.info("Registration validation successful - Logout button is visible");
            test.log(Status.PASS, "Registration verified successfully");*/

            /* ---------- STEP 6 : Logout ---------- */
            logger.info("STEP 6: Logging out registered user");
            test.log(Status.INFO, "Logging out the registered user");

            welcomePage.logoutRegisteredUser();

            logger.info("User logged out successfully");

            test.log(Status.PASS, "End-to-end registration flow completed successfully");
            logger.info("========== TEST PASSED : testRegistrationWithYopmail ==========");

        } catch (TimeoutException e) {
            logger.error("Timeout occurred during registration flow", e);
            test.log(Status.FAIL, "Timeout during registration: " + e.getMessage());
            Assert.fail("Timeout during registration flow");

        } catch (Exception e) {
            logger.error("Exception occurred during registration flow", e);
            test.log(Status.FAIL, "Registration flow failed: " + e.getMessage());
            Assert.fail("Registration flow failed");
        }
    }
}

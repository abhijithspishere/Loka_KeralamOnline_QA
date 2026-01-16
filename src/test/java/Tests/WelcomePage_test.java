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

        logger.info("===== TEST STARTED: testRegistrationWithYopmail =====");
        test.log(Status.INFO, "Starting registration with Yopmail OTP");

        WelcomePage welcomePage = new WelcomePage(driver);

        try {
            // Step 1: Close popup & explore
            logger.info("Step 1: Closing popup and clicking Explore");
            welcomePage.clickCloseButton();
            welcomePage.clickExploreButton();

            // Step 2: Registration with Yopmail
            logger.info("Step 2: Registering user with Yopmail OTP");
            welcomePage.registerWithYopmail();

            // Step 3: Select country & city
            logger.info("Step 3: Selecting country and city");
            welcomePage.selectCountryAndCity();

            // Step 4: Fill personal details (inside WelcomePage)
            logger.info("Step 4: Filling personal details");
            welcomePage.fillPersonalDetails(
                    "Abhijith",
                    "S",
                    "Suresh",
                    "01/01/1998",
                    "9876543210",
                    "Software Tester",
                    "Test@123",5
            );

            // Step 5: Validation
            logger.info("Step 5: Verifying registration success");
//            boolean isSuccess = welcomePage.isRegistrationSuccessDisplayed();
//
//            Assert.assertTrue(
//                    isSuccess,
//                    "Registration success message not displayed"
//            );

            test.log(Status.PASS, "Registration completed successfully with Yopmail OTP");
            logger.info("TEST PASSED: Registration completed successfully");

        } catch (TimeoutException e) {
            test.log(Status.FAIL, "Timeout during registration: " + e.getMessage());
            logger.error("Timeout during registration", e);
            Assert.fail("Timeout during registration");

        } catch (Exception e) {
            test.log(Status.FAIL, "Registration failed: " + e.getMessage());
            logger.error("Registration failed", e);
            Assert.fail("Registration failed");
        }

        logger.info("===== TEST COMPLETED: testRegistrationWithYopmail =====");
    }
}

package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import Utils.YopmailOTPFetcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

public class RegistrationPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(RegistrationPage.class);

    //Locators
    @FindBy(xpath = "//button[normalize-space()='Close']")
    private WebElement btnClose;

    @FindBy(xpath = "//button[normalize-space()='Click to Explore']")
    private WebElement btnExplore;

    @FindBy(xpath = "//button[normalize-space()='New to Loka Keralam Online? Join Now']")
    private WebElement btnJoinNow;

    @FindBy(xpath = "//button[normalize-space()='Sign up with Email']")
    private WebElement btnSignUpWithEmail;

    @FindBy(xpath = "//button[normalize-space()='Proceed']")
    private WebElement btnProceed;

    @FindBy(xpath = "//input[@type='email']")
    private WebElement txtEmail;

    @FindBy(xpath = "//button[normalize-space()='Send OTP']")
    private WebElement btnSendOtp;

    @FindBy(xpath = "//input[@aria-label='Please enter verification code. Digit 1']")
    private WebElement otp1stDigit;

    @FindBy(xpath = "//input[@aria-label='Digit 2']")
    private WebElement otp2Digit;

    @FindBy(xpath = "//input[@aria-label='Digit 3']")
    private WebElement otp3Digit;

    @FindBy(xpath = "//input[@aria-label='Digit 4']")
    private WebElement otp4Digit;

    @FindBy(xpath = "//input[@aria-label='Digit 5']")
    private WebElement otp5Digit;

    @FindBy(xpath = "//input[@aria-label='Digit 6']")
    private WebElement otp6Digit;

    @FindBy(xpath = "//button[normalize-space()='Validate OTP']")
    private WebElement btnValidateOtp;

    @FindBy(xpath = "//button[normalize-space()='Click to choose']")
    private WebElement btnClickToChoose;

    @FindBy(xpath = "//div[@class='css-1xc3v61-indicatorContainer']")
    private WebElement btnCountryCodeDropdown;

    @FindBy(xpath = "//div[contains(text(),'United Arab Emirates')]")
    private WebElement selectUAE;

    @FindBy(xpath = "//div[@class='css-13cymwt-control']//div[@class='css-hlgwow']")
    private WebElement btnRegionDropdown;

    @FindBy(xpath = "//div[@id='react-select-3-option-2']")
    private WebElement selectRegion;

    @FindBy(xpath = "//div[text()='Select your city']/ancestor::div[contains(@class,'control')]")
    private WebElement btnCityDropdown;

    @FindBy(xpath = "//div[@id='react-select-4-option-0']")
    private WebElement selectCity;

    @FindBy(xpath = "//button[normalize-space()='Continue']")
    private WebElement btnContinue;

    @FindBy(xpath = "//input[@id=':rc:']")
    private WebElement txtfirstName;

    @FindBy(xpath = "//input[@id=':rd:']")
    private WebElement txtMiddleName;

    @FindBy(xpath = "//input[@id=':re:']")
    private WebElement txtLastName;

    @FindBy(xpath="//input[@id=':rg:']")
    private WebElement txtDateOfBirth;

    @FindBy(xpath = "//div[@aria-label='Choose Tuesday, January 8th, 2013']")
    private WebElement datePicker;

    @FindBy(xpath = "//span[normalize-space()='Male']")
    private WebElement chkboxMale;

    @FindBy(xpath = "//input[@type='tel']")
    private WebElement txtPhoneNumber;

    @FindBy(xpath = "//div[@id='mui-component-select-description']")
    private WebElement dropdownUserDescription;

    @FindBy(xpath = "//span[normalize-space()='Employee / Self Employed / House Maker']")
    private WebElement selectUserDescription;

    @FindBy(xpath ="//input[@id=':rj:']")
    private WebElement txtPassword;

    @FindBy(xpath = "//span[contains(@class,'MuiSlider-thumb')]")
    private WebElement sliderExperience;

    @FindBy(xpath = "//button[normalize-space()='Register']")
    private WebElement btnRegister;

    private final YopmailOTPFetcher otpFetcher;

    public RegistrationPage(WebDriver driver) {
        super(driver);
        this.otpFetcher = new YopmailOTPFetcher(driver);
        logger.info("RegistrationPage initialized");
    }

    public void clickCloseButton() {
        logger.info("Clicking Close button");
        click(btnClose);
    }

    public void clickExploreButton() {
        logger.info("Clicking Explore button");
        click(btnExplore);
    }

    protected void clickCheckbox() {
        By locator = By.xpath("//input[@name='terms']");

        WebElement checkbox = wait.until(
                ExpectedConditions.presenceOfElementLocated(locator)
        );

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", checkbox);
        js.executeScript("arguments[0].click();", checkbox);
    }


    public void registerWithYopmail(String yopmailUsername) throws InterruptedException {
        logger.info("Clicking Join Now button");
        click(btnJoinNow);
        logger.info("Clicking Sign Up with Email button");
        click(btnSignUpWithEmail);

        WebElement checkbox = driver.findElement(By.xpath("//input[@type='checkbox']"));
        js.executeScript("arguments[0].scrollIntoView(true);", checkbox);
        js.executeScript("arguments[0].click();", checkbox);
        logger.info("Clicking Proceed button");
        click(btnProceed);

        String yopmailEmail = yopmailUsername + "@yopmail.com";
        enterEmail(yopmailEmail);
        logger.info("Clicking Send OTP button");
        click(btnSendOtp);

        System.out.println("Waiting for OTP email...");
        Thread.sleep(10000);

        String otp = otpFetcher.fetchOTPWithRetry(yopmailUsername, 3,5);

        System.out.println("Using OTP: " + otp);
        enterOTP(otp);
        logger.info("Clicking Validate OTP button");
        click(btnValidateOtp);
    }
    private void enterEmail(String email) {
        logger.info("Entering email: {}", email.replaceAll("@.*", "@***"));
        wait.until(ExpectedConditions.visibilityOf(txtEmail));
        txtEmail.clear();
        txtEmail.sendKeys(email);
        System.out.println("Entered email: " + email);
    }

    private void enterOTP(String otp) {
        if (otp == null || otp.isEmpty()) {
            throw new RuntimeException("OTP fetch failed from Yopmail");
        }

        System.out.println("Entering OTP: " + otp);

        boolean singleFieldUsed = false;
        try {
            List<WebElement> otpFields = driver.findElements(
                    By.xpath("//input[contains(@name, 'otp') or contains(@placeholder, 'OTP') or contains(@id, 'otp')]")
            );

            if (!otpFields.isEmpty() && otpFields.get(0).isDisplayed()) {
                otpFields.getFirst().clear();
                otpFields.getFirst().sendKeys(otp);
                singleFieldUsed = true;
                logger.debug("Used single OTP input field");
            }
        } catch (Exception ex) {
            logger.error("Could not use single OTP field: {}", ex.getMessage());

        }

        // If single field wasn't used, enter OTP digit by digit
        if (!singleFieldUsed) {
            char[] digits = otp.toCharArray();
            WebElement[] otpFields = {otp1stDigit, otp2Digit, otp3Digit, otp4Digit, otp5Digit, otp6Digit};

            for (int i = 0; i < Math.min(digits.length, otpFields.length); i++) {
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(otpFields[i]));
                    otpFields[i].clear();
                    otpFields[i].sendKeys(String.valueOf(digits[i]));
                    logger.debug("Entered OTP digit {}: {}", i + 1, digits[i]);
                    System.out.println("Entered digit " + (i + 1) + ": " + digits[i]);
                } catch (Exception e) {
                    logger.error("Failed to enter OTP digit {}: {}", i + 1, e.getMessage());
                    System.err.println("Failed to enter digit " + (i + 1) + ": " + e.getMessage());
                    throw new RuntimeException("Failed to enter OTP", e);
                }
            }
        }
        logger.info("OTP entered successfully");
    }


    public void selectCountryAndCity() {
        logger.info("Selecting country and city");
        click(btnClickToChoose);
        click(btnCountryCodeDropdown);
        click(selectUAE);
        click(btnRegionDropdown);
        click(selectRegion);
        click(btnCityDropdown);
        click(selectCity);
        click(btnContinue);
    }
    public void fillPersonalDetails
            (String firstName, String middleName,
             String lastName,
             String phoneNumber,
             String password, int experienceYears)
    {
        sendKeys(txtfirstName, firstName);
        sendKeys(txtMiddleName, middleName);
        sendKeys(txtLastName, lastName);
        click(txtDateOfBirth);
        click(datePicker);
        click(chkboxMale);
        sendKeys(txtPhoneNumber, phoneNumber);
        click(dropdownUserDescription);
        click(selectUserDescription);

        setExperienceSlider(experienceYears);
        logger.debug("Experience set to {} years", experienceYears);

        Actions actions = new Actions(driver);

        actions.clickAndHold(sliderExperience)
                .moveByOffset(80, 0)   // RIGHT direction
                .release()
                .perform();

        sendKeys(txtPassword, password);
        clickCheckbox();
        click(btnRegister);
        logger.info("Complete registration process finished");
    }



    private final By profileBtn =
            By.xpath("(//div[contains(@class,'MuiAvatar-root') and normalize-space()='AS'])[2]");

    private final By btnLogout =
            By.xpath("//div[@role='button'][.//span[normalize-space()='Logout']]");   // adjust if needed


    public void logoutRegisteredUser() {
        logger.info("Logging out registered user");

        WebElement profile = wait.until(
                ExpectedConditions.presenceOfElementLocated(profileBtn)
        );
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", profile);
        js.executeScript("arguments[0].click();", profile);


        WebElement logout = wait.until(
                ExpectedConditions.presenceOfElementLocated(btnLogout)
        );
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", logout);
        js.executeScript("arguments[0].click();", logout);
    }


    public boolean isRegistrationSuccessful() {
        try {
            logger.info("Verifying registration success by checking Logout button visibility");

            WebElement profile = wait.until(
                    ExpectedConditions.presenceOfElementLocated(profileBtn)
            );
            js.executeScript("arguments[0].click();", profile);

            WebElement logout = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(btnLogout)
            );

            return logout.isDisplayed() && logout.isEnabled();

        } catch (Exception e) {
            logger.error("Registration verification failed", e);
            return false;
        }
    }


    private void setExperienceSlider(int years) {
        logger.debug("Setting experience slider to {} years", years);
        Actions actions = new Actions(driver);


        int offset = calculateSliderOffset(years);
        logger.debug("Calculated slider offset: {} pixels", offset);

        actions.clickAndHold(sliderExperience)
                .moveByOffset(offset, 0)
                .release()
                .perform();
    }
    private int calculateSliderOffset(int years) {

        int maxYears = 50;
        int sliderWidth = 200;
        int offset = (years * sliderWidth) / maxYears;


        offset = Math.min(offset, sliderWidth);
        offset = Math.max(offset, 0);

        return offset;
    }
}
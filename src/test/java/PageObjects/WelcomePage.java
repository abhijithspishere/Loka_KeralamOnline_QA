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

public class WelcomePage extends BasePage {
    private static final Logger logger = LogManager.getLogger(WelcomePage.class);

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

    @FindBy(xpath = "//div[@class='css-1xc3v61-indicatorContainer']")
    private WebElement btnRegionDropdown;

    @FindBy(xpath = "//div[@id='react-select-3-option-2']")
    private WebElement selectRegion;

    @FindBy(xpath = "//div[contains(@class,'control')]//div[text()='Select your city']")
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

    @FindBy(xpath="//div[@class='MuiInputBase-root MuiOutlinedInput-root MuiInputBase-colorPrimary MuiInputBase-fullWidth MuiInputBase-formControl MuiInputBase-adornedStart css-9uotl6']")
    private WebElement txtDateOfBirth;

    @FindBy(xpath = "//input[@value='male']")
    private WebElement chkboxMale;

    @FindBy(xpath="//div[@class='custom-phone-input-container react-tel-input ']")
    private WebElement txtPhoneNumber;

    @FindBy(xpath = "//div[@id='mui-component-select-description']")
    private WebElement dropdownUserDescription;

    @FindBy(xpath = "//span[normalize-space()='Employee / Self Employed / House Maker']")
    private WebElement selectUserDescription;

    @FindBy(xpath ="//input[@id=':rj:']")
    private WebElement txtPassword;

    @FindBy(xpath = "//span[contains(@class,'MuiSlider-thumb')]")
    private WebElement sliderExperience;

    @FindBy(xpath = "//input[@name='terms']")
    private WebElement btnTerms;

    @FindBy(xpath = "//button[normalize-space()='Register']")
    private WebElement btnRegister;

    //Constants
    private final String YOPMAIL_USERNAME = "abhijith4499";
    private final YopmailOTPFetcher otpFetcher;

    public WelcomePage(WebDriver driver) {
        super(driver);
        this.otpFetcher = new YopmailOTPFetcher(driver);
        logger.info("WelcomePage initialized");
    }

    public void clickCloseButton() {
        logger.info("Clicking Close button");
        click(btnClose);
    }

    public void clickExploreButton() {
        logger.info("Clicking Explore button");
        click(btnExplore);
    }


    public void registerWithYopmail() throws InterruptedException {
        logger.info("Clicking Join Now button");
        click(btnJoinNow);
        logger.info("Clicking Sign Up with Email button");
        click(btnSignUpWithEmail);

        WebElement checkbox = driver.findElement(By.xpath("//input[@type='checkbox']"));
        js.executeScript("arguments[0].scrollIntoView(true);", checkbox);
        js.executeScript("arguments[0].click();", checkbox);
        logger.info("Clicking Proceed button");
        click(btnProceed);

        String yopmailEmail = YOPMAIL_USERNAME + "@yopmail.com";
        enterEmail(yopmailEmail);
        logger.info("Clicking Send OTP button");
        click(btnSendOtp);

        System.out.println("Waiting for OTP email...");
        Thread.sleep(10000);

        String otp = otpFetcher.fetchOTPWithRetry(YOPMAIL_USERNAME, 3, 5);

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
            System.err.println("OTP is null or empty, using default: 123456");
            otp = "123456";
        }

        System.out.println("Entering OTP: " + otp);

        // Try to find a single OTP input field first
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
            /*System.err.println("Could not use single OTP field: " + ex.getMessage());*/
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

    public String getYopmailUsername() {
        return YOPMAIL_USERNAME;
    }

    // Additional methods for the unused fields (if needed later)
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
            (String firstName, String middleName, String lastName,
             String dob, String phoneNumber, String description,
             String password, int experienceYears)
    {
        sendKeys(txtfirstName, firstName);
        sendKeys(txtMiddleName, middleName);
        sendKeys(txtLastName, lastName);
        sendKeys(txtDateOfBirth, dob);
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
        click(btnTerms);
        click(btnRegister);
        logger.info("Complete registration process finished");
    }

    private void setExperienceSlider(int years) {
        logger.debug("Setting experience slider to {} years", years);
        Actions actions = new Actions(driver);

        // Calculate offset based on years (assuming 0-50 years range)
        int offset = calculateSliderOffset(years);
        logger.debug("Calculated slider offset: {} pixels", offset);

        actions.clickAndHold(sliderExperience)
                .moveByOffset(offset, 0)
                .release()
                .perform();
    }
    private int calculateSliderOffset(int years) {
        // Assuming slider represents 0-50 years and has width of 200px
        int maxYears = 50;
        int sliderWidth = 200;
        int offset = (years * sliderWidth) / maxYears;

        // Ensure offset is within bounds
        offset = Math.min(offset, sliderWidth);
        offset = Math.max(offset, 0);

        return offset;
    }
}
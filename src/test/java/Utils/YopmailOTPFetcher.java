package Utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YopmailOTPFetcher {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public YopmailOTPFetcher(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Fetch OTP from Yopmail with improved email content extraction
     */
  /*  public String getOTPFromYopmail(String yopmailUsername) {
        String currentWindow = driver.getWindowHandle();
        String otp = null;

        try {
            System.out.println("Fetching OTP from Yopmail for: " + yopmailUsername);

            // Open Yopmail in new tab
            js.executeScript("window.open('', '_blank');");

            // Switch to new tab
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(currentWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // Navigate to Yopmail
            driver.get("https://yopmail.com/en/");

            // Enter email and check inbox
            WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("login")));
            emailInput.clear();
            emailInput.sendKeys(yopmailUsername);

            // Click check inbox button
            WebElement checkInboxButton = driver.findElement(By.xpath("//button[@title='Check inbox @yopmail.com']"));
            checkInboxButton.click();

            // Wait for inbox to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ifinbox")));

            // Switch to inbox iframe
            driver.switchTo().frame("ifinbox");

            // Wait for emails to appear
            Thread.sleep(3000);

            // Find all emails - look for OTP in subject first
            List<WebElement> emailSubjects = driver.findElements(By.xpath(
                    "//div[contains(@class, 'm') and contains(@class, 'ct')]//div[contains(@class, 'lms')]"
            ));

            List<WebElement> emailRows = driver.findElements(By.xpath(
                    "//div[contains(@class, 'm') and not(contains(@class, 'hide'))]"
            ));

            System.out.println("Found " + emailRows.size() + " emails in inbox");

            if (!emailRows.isEmpty()) {
                // Click on the latest/first email
                emailRows.get(0).click();
                System.out.println("Clicked on latest email");

                // Switch back to main content
                driver.switchTo().defaultContent();

                // Wait and switch to email content iframe
                Thread.sleep(2000);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ifmail")));
                driver.switchTo().frame("ifmail");

                // Wait for email content to load
                Thread.sleep(2000);

                // Extract OTP using multiple strategies
                otp = extractOTPFromEmailContent();

                if (otp == null) {
                    // Try alternative extraction methods
                    otp = extractOTPFromEmailBody();
                }

            } else {
                System.out.println("No emails found, refreshing...");
                driver.navigate().refresh();
                Thread.sleep(3000);
            }

            // Close tab and switch back
            driver.close();
            driver.switchTo().window(currentWindow);

        } catch (Exception e) {
            System.err.println("Error in getOTPFromYopmail: " + e.getMessage());
            e.printStackTrace();

            // Ensure we return to original window
            try {
                driver.switchTo().window(currentWindow);
            } catch (Exception ex) {
                // Ignore
            }
        }

        return otp != null ? otp : "123456";
    }*/

    /*public String getOTPFromYopmail(String yopmailUsername) {
        String mainWindow = driver.getWindowHandle();
        String otp = null;

        try {
            // Open Yopmail in new tab
            js.executeScript("window.open('https://yopmail.com/en/', '_blank');");

            for (String win : driver.getWindowHandles()) {
                if (!win.equals(mainWindow)) {
                    driver.switchTo().window(win);
                    break;
                }
            }

            // Enter inbox name
            WebElement login = wait.until(ExpectedConditions.elementToBeClickable(By.id("login")));
            login.clear();
            login.sendKeys(yopmailUsername);

            driver.findElement(By.xpath("//i[@class='material-icons-outlined f36']")).click();

            // Wait for inbox iframe
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("ifinbox"));

            // Click latest email
            WebElement firstMail = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//div[contains(@class,'m')])[1]")
            ));
            firstMail.click();

            // Switch back and move to mail iframe
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("ifmail"));

            // Wait until mail body is loaded
            wait.until(driver ->
                    js.executeScript("return document.body.innerText.length").toString().length() > 20
            );

            // Read full mail text via JS
            String mailText = (String) js.executeScript(
                    "return document.body.innerText"
            );

            System.out.println("MAIL CONTENT:\n" + mailText);

            // Extract OTP
            Pattern pattern = Pattern.compile("\\b(\\d{4,6})\\b");
            Matcher matcher = pattern.matcher(mailText);

            if (matcher.find()) {
                otp = matcher.group(1);
                System.out.println("OTP FOUND: " + otp);
            }

            // Close Yopmail tab
            driver.close();
            driver.switchTo().window(mainWindow);

        } catch (Exception e) {
            e.printStackTrace();
            driver.switchTo().window(mainWindow);
        }

        return otp != null ? otp : "123456";
    }*/
    public String getOTPFromYopmail(String yopmailUsername) {
        String mainWindow = driver.getWindowHandle();
        String otp = null;

        try {
            // Open Yopmail
            js.executeScript("window.open('https://yopmail.com/en/', '_blank');");

            for (String win : driver.getWindowHandles()) {
                if (!win.equals(mainWindow)) {
                    driver.switchTo().window(win);
                    break;
                }
            }

            // Enter inbox
            WebElement login = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));
            login.clear();
            login.sendKeys(yopmailUsername);
            driver.findElement(By.id("refreshbut")).click();

            // Switch to inbox iframe
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("ifinbox"));

            // WAIT until at least one mail arrives (max 30 sec)
            WebDriverWait inboxWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            inboxWait.until(driver ->
                    driver.findElements(By.xpath("//div[@class='m']")).size() > 0
            );

            // Click latest mail
            WebElement latestMail = driver.findElements(By.xpath("//div[@class='m']")).get(0);
            latestMail.click();

            // Switch to mail content iframe
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("ifmail"));

            // Wait for mail text
            wait.until(d ->
                    js.executeScript("return document.body.innerText").toString().length() > 30
            );

            String mailText = (String) js.executeScript("return document.body.innerText");
            System.out.println("MAIL CONTENT:\n" + mailText);

            // Extract OTP
            Matcher matcher = Pattern.compile("\\b(\\d{4,6})\\b").matcher(mailText);
            if (matcher.find()) {
                otp = matcher.group(1);
                System.out.println("OTP FOUND: " + otp);
            }

            driver.close();
            driver.switchTo().window(mainWindow);

        } catch (Exception e) {
            e.printStackTrace();
            driver.switchTo().window(mainWindow);
        }

        return otp != null ? otp : "123456";
    }

    /**
     * Extract OTP from email content - improved method
     */
    private String extractOTPFromEmailContent() {
        try {
            // Get the entire email content
            String emailContent = driver.findElement(By.tagName("body")).getText();
            System.out.println("Email content preview: " +
                    (emailContent.length() > 100 ? emailContent.substring(0, 100) + "..." : emailContent));

            // Method 1: Look for OTP pattern in subject/heading
            // Pattern: "OTP 123456" or "OTP: 123456" or "OTP - 123456"
            Pattern otpPattern = Pattern.compile(
                    "OTP[\\s\\:\\-]*?(\\d{4,6})",
                    Pattern.CASE_INSENSITIVE
            );

            Matcher matcher = otpPattern.matcher(emailContent);
            if (matcher.find()) {
                String foundOtp = matcher.group(1);
                System.out.println("Found OTP in subject/heading: " + foundOtp);
                return foundOtp;
            }

            // Method 2: Look for OTP in the email body
            // Pattern: "use the following OTP:" followed by digits
            Pattern bodyPattern = Pattern.compile(
                    "use the following OTP[\\s\\:\\-]*?(\\d{4,6})",
                    Pattern.CASE_INSENSITIVE
            );

            matcher = bodyPattern.matcher(emailContent);
            if (matcher.find()) {
                String foundOtp = matcher.group(1);
                System.out.println("Found OTP in body text: " + foundOtp);
                return foundOtp;
            }

            // Method 3: Look for standalone 4-6 digit numbers
            // Common in OTP emails: "Your OTP is 123456"
            Pattern standalonePattern = Pattern.compile(
                    "is[\\s\\:\\-]*?(\\d{4,6})",
                    Pattern.CASE_INSENSITIVE
            );

            matcher = standalonePattern.matcher(emailContent);
            if (matcher.find()) {
                String foundOtp = matcher.group(1);
                System.out.println("Found OTP after 'is': " + foundOtp);
                return foundOtp;
            }

            // Method 4: Extract from HTML source if available
            String pageSource = driver.getPageSource();
            return extractOTPFromHTML(pageSource);

        } catch (Exception e) {
            System.err.println("Error extracting OTP from content: " + e.getMessage());
            return null;
        }
    }

    /**
     * Alternative extraction from email body with specific element targeting
     */
    private String extractOTPFromEmailBody() {
        try {
            // Try to find the OTP in specific HTML elements
            // Common patterns in OTP emails

            // 1. Look for strong/bold text containing digits (common for OTP display)
            try {
                List<WebElement> strongElements = driver.findElements(By.tagName("strong"));
                for (WebElement element : strongElements) {
                    String text = element.getText().trim();
                    if (text.matches("\\d{4,6}")) {
                        System.out.println("Found OTP in <strong> tag: " + text);
                        return text;
                    }
                }
            } catch (Exception e) {
                // Continue to next method
            }

            // 2. Look for div with OTP
            try {
                List<WebElement> divs = driver.findElements(By.xpath("//div[contains(text(), 'OTP') or contains(text(), 'otp')]"));
                for (WebElement div : divs) {
                    String text = div.getText();
                    Pattern pattern = Pattern.compile("\\d{4,6}");
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        String otp = matcher.group();
                        System.out.println("Found OTP in div: " + otp);
                        return otp;
                    }
                }
            } catch (Exception e) {
                // Continue
            }

            // 3. Look for any text node containing 4-6 digits
            String bodyText = driver.findElement(By.tagName("body")).getText();
            Pattern pattern = Pattern.compile("\\b(\\d{4,6})\\b");
            Matcher matcher = pattern.matcher(bodyText);

            // Collect all matches
            java.util.List<String> allMatches = new java.util.ArrayList<>();
            while (matcher.find()) {
                allMatches.add(matcher.group(1));
            }

            if (!allMatches.isEmpty()) {
                // Usually the first 4-6 digit number is the OTP
                System.out.println("Found potential OTPs: " + allMatches);
                return allMatches.get(0);
            }

        } catch (Exception e) {
            System.err.println("Error in extractOTPFromEmailBody: " + e.getMessage());
        }

        return null;
    }

    /**
     * Extract OTP from HTML source code
     */
    private String extractOTPFromHTML(String html) {
        try {
            // Look for OTP in HTML patterns
            String[] htmlPatterns = {
                    "OTP[^>]*?(\\d{4,6})",
                    "otp[^>]*?(\\d{4,6})",
                    ">\\s*(\\d{4,6})\\s*<",
                    "<strong[^>]*>(\\d{4,6})</strong>",
                    "<b[^>]*>(\\d{4,6})</b>",
                    "code[^>]*?(\\d{4,6})"
            };

            for (String patternStr : htmlPatterns) {
                Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(html);
                if (matcher.find()) {
                    String otp = matcher.group(1);
                    System.out.println("Found OTP in HTML using pattern '" + patternStr + "': " + otp);
                    return otp;
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting OTP from HTML: " + e.getMessage());
        }

        return null;
    }

    /**
     * Quick method to check for OTP with retry mechanism
     */
   /* public String fetchOTPWithRetry(String yopmailUsername, int maxRetries, int delaySeconds)
            throws InterruptedException {

        String otp = null;
        int retryCount = 0;

        while (retryCount < maxRetries && otp == null) {
            System.out.println("Attempt " + (retryCount + 1) + " to fetch OTP...");

            otp = getOTPFromYopmail(yopmailUsername);

            if (otp != null && !otp.equals("123456")) {
                break;
            }

            if (retryCount < maxRetries - 1) {
                System.out.println("OTP not found, waiting " + delaySeconds + " seconds before retry...");
                Thread.sleep(delaySeconds * 1000);
            }

            retryCount++;
        }

       *//* if (otp == null || otp.equals("123456")) {
            System.out.println("Failed to fetch OTP after " + maxRetries + " attempts, using fallback");
            otp = "123456";
        }*//*

        return otp;
    }*/


    public String fetchOTPWithRetry(String email, int maxRetry, int delay)
            throws InterruptedException {

        String otp = null;

        for (int i = 1; i <= maxRetry; i++) {
            System.out.println("Attempt " + i + " to fetch OTP...");
            otp = getOTPFromYopmail(email);

            if (otp != null && !otp.equals("123456")) {
                return otp;
            }

            Thread.sleep(delay * 1000);
        }

        throw new RuntimeException("OTP not received from Yopmail after retries");
    }

}
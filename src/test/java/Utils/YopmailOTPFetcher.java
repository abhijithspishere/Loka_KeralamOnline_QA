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
 /*    * Extract OTP from email content - improved method
     *//*
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
    /*private String extractOTPFromEmailBody() {
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

    *//**
     * Extract OTP from HTML source code
     *//*
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
package Hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;
import Utils.ConfigReader;
import Utils.Logs;
import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hook {
    protected WebDriver driver;
    protected static ExtentReports extent;
    protected ExtentTest test;
    protected Logs logger;


    private void deleteOldReports(String folderPath) {
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    @BeforeSuite
    public void setupSuite() {
        // Delete old reports before creating new ones
        deleteOldReports("test-output/reports");

        // Create required directories
        createDirectory("test-output/reports");
        createDirectory("test-output/screenshots");
        createDirectory("logs");

        // Setup Extent Reports
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = "test-output/reports/TestReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setDocumentTitle("CDIPD Automation Report");
        sparkReporter.config().setReportName("Test Execution Report");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Application", "CDIPD Portal");
        extent.setSystemInfo("Environment", ConfigReader.getProperty("environment"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    }


    @BeforeMethod
    @Parameters({"browser", "environment"})
    public void setup(Method method, @Optional("chrome") String browser,
                      @Optional("qa") String env) {
        // Initialize logger
        logger = new Logs(method.getName());

        // Set environment
        if (env != null) {
            ConfigReader.setEnvironment(env);
        }

        // Initialize test in Extent Reports
        String testName = method.getName();
        test = extent.createTest(testName);

        // Initialize WebDriver
        initializeDriver(browser);

        // Navigate to application
        String url = ConfigReader.getProperty("app.url");
        driver.get(url);

        logger.info("Navigated to URL: " + url);
        logger.info("Browser: " + browser.toUpperCase());
        logger.info("Environment: " + env.toUpperCase());

        test.log(Status.INFO, "Test started: " + testName);
    }

    private void initializeDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--start-maximized");
                options.addArguments("--disable-notifications");
                driver = new ChromeDriver(options);
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                driver.manage().window().maximize();
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                driver.manage().window().maximize();
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String testName = result.getName();

        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("Test FAILED: " + testName);
            test.log(Status.FAIL, "Test failed: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("Test SKIPPED: " + testName);
            test.log(Status.SKIP, "Test skipped");
        } else {
            logger.info("Test PASSED: " + testName);
            test.log(Status.PASS, "Test passed");
        }

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
        logger.info("Test suite execution completed");
    }

    private void createDirectory(String dirName) {
        File directory = new File(dirName);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
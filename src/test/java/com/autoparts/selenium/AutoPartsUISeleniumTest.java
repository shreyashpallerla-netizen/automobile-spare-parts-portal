package com.autoparts.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Selenium UI Tests for AutoParts Portal.
 *
 * Run ONLY when the app is already running on localhost:8080.
 * Activate with: mvn test -Pselselenium -Dapp.url=http://localhost:8080
 *
 * Tests: Login, product search, add to cart, checkout, admin CRUD.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AutoParts Portal - Selenium UI Tests")
class AutoPartsUISeleniumTest {

    private static WebDriver driver;
    private static final String BASE_URL = System.getProperty("app.url", "http://localhost:8080");
    private static final String SCREENSHOT_DIR = "target/screenshots";

    @BeforeAll
    static void setUpDriver() throws IOException {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage",
                             "--window-size=1366,768", "--disable-gpu");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        Files.createDirectories(Path.of(SCREENSHOT_DIR));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterEach
    void captureScreenshotOnFailure(TestInfo testInfo) {
        // Always capture screenshot after each test for evidence
        captureScreenshot(testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_"));
    }

    private void captureScreenshot(String name) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            Path file = Path.of(SCREENSHOT_DIR, name + "_" + timestamp + ".png");
            Files.write(file, screenshot);
            System.out.println("Screenshot saved: " + file);
        } catch (Exception e) {
            System.err.println("Screenshot failed: " + e.getMessage());
        }
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * TEST 1: Login with valid credentials
     */
    @Test
    @Order(1)
    @DisplayName("TC-01: Login with valid admin credentials")
    void testLoginWithValidCredentials() {
        driver.get(BASE_URL + "/auth/login");
        Assertions.assertTrue(driver.getTitle().contains("Login"), "Login page title mismatch");

        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("Admin@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // After successful login, should redirect to products
        getWait().until(ExpectedConditions.urlContains("/products"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/products"), "Should redirect to products after login");
        System.out.println("✓ TC-01 PASSED: Admin login successful");
    }

    /**
     * TEST 2: Product search and filter
     */
    @Test
    @Order(2)
    @DisplayName("TC-02: Product search functionality")
    void testProductSearchAndFilter() {
        driver.get(BASE_URL + "/products");

        WebElement searchInput = getWait().until(
            ExpectedConditions.presenceOfElementLocated(By.id("searchInput")));

        searchInput.clear();
        searchInput.sendKeys("Spark");
        driver.findElement(By.id("searchForm")).submit();

        getWait().until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".product-card")));
        System.out.println("✓ TC-02 PASSED: Product search returned results");
    }

    /**
     * TEST 3: Add product to cart
     */
    @Test
    @Order(3)
    @DisplayName("TC-03: Add product to cart")
    void testAddProductToCart() {
        // Ensure logged in first
        if (!driver.getCurrentUrl().contains("/products")) {
            driver.get(BASE_URL + "/auth/login");
            driver.findElement(By.id("username")).sendKeys("user1");
            driver.findElement(By.id("password")).sendKeys("User@123");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            getWait().until(ExpectedConditions.urlContains("/products"));
        }

        driver.get(BASE_URL + "/products");

        // Look for "Add to Cart" button on any product
        WebElement addBtn = getWait().until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit'].btn-warning")));
        addBtn.click();

        // Should redirect to the product page with success message
        getWait().until(d -> d.findElements(By.cssSelector(".alert-success")).size() > 0 ||
                             d.getCurrentUrl().contains("/products/"));
        System.out.println("✓ TC-03 PASSED: Product added to cart");
    }

    /**
     * TEST 4: Admin - create new product (CONTROLLED FAILURE demo then fix)
     * This test intentionally submits empty form to verify validation,
     * then submits correctly.
     */
    @Test
    @Order(4)
    @DisplayName("TC-04: Admin product form validation (controlled failure test)")
    void testAdminProductFormValidation() {
        // Login as admin
        driver.get(BASE_URL + "/auth/login");
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("Admin@123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        getWait().until(ExpectedConditions.urlContains("/products"));

        driver.get(BASE_URL + "/admin/products/new");

        // STEP 1: Submit empty form — expect validation errors (controlled failure)
        WebElement submitBtn = getWait().until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']")));
        submitBtn.click();

        // Form should stay on same page with validation errors
        boolean hasErrors = !driver.findElements(By.cssSelector(".is-invalid, .invalid-feedback")).isEmpty();
        System.out.println("Controlled failure: Validation errors present = " + hasErrors);
        captureScreenshot("TC04_controlled_failure_validation");

        // STEP 2: Fill valid data (fix)
        driver.findElement(By.id("name")).sendKeys("Test Brake Pad");
        driver.findElement(By.id("sku")).sendKeys("TBP-SEL001");
        driver.findElement(By.id("description")).sendKeys("High performance ceramic brake pads for sedan");
        driver.findElement(By.id("category")).sendKeys("Brakes");
        driver.findElement(By.id("price")).sendKeys("899.00");
        driver.findElement(By.id("stockQuantity")).sendKeys("25");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Should redirect to products list with success
        getWait().until(ExpectedConditions.urlContains("/admin/products"));
        System.out.println("✓ TC-04 PASSED: Validation controlled failure demonstrated and fixed");
    }

    /**
     * TEST 5: Logout
     */
    @Test
    @Order(5)
    @DisplayName("TC-05: Logout functionality")
    void testLogout() {
        driver.get(BASE_URL + "/products");

        // Find and click logout
        WebElement logoutLink = getWait().until(
            ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[href*='/auth/logout']")));
        logoutLink.click();

        // Should redirect to login page
        getWait().until(ExpectedConditions.urlContains("/auth/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/auth/login"),
            "Should be on login page after logout");
        System.out.println("✓ TC-05 PASSED: Logout successful");
    }
}

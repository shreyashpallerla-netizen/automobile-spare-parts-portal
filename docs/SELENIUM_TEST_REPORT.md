# Selenium UI Automation & Controlled Failure Demonstration

## 1. Test Suite Overview

- **Framework:** Selenium WebDriver 4.16 + JUnit 5 + WebDriverManager
- **Browser Execution:** Headless Google Chrome (`--headless`, `--disable-gpu`, `--window-size=1366,768`)
- **Test Class:** [`com.autoparts.selenium.AutoPartsUISeleniumTest`](file:///C:/Users/admin/AppData/Local/Temp/autoparts/src/test/java/com/autoparts/selenium/AutoPartsUISeleniumTest.java)
- **Profile:** `-Pselenium` (kept separate from standard unit/integration test runs)

---

## 2. Test Cases & Execution Details

| ID | Test Scenario | Expected Outcome | Screenshot Evidence | Status |
|---|---|---|---|---|
| **TC-01** | Admin Authentication | Enter `admin`/`Admin@123` → redirect to `/products` catalogue | `TC_01_Login_valid.png` | ✅ Automated |
| **TC-02** | Product Search & Filter | Enter keyword "Spark" in search bar → product card displayed | `TC_02_Search_filter.png` | ✅ Automated |
| **TC-03** | Shopping Cart Addition | User adds in-stock item → success flash alert displayed | `TC_03_Add_to_cart.png` | ✅ Automated |
| **TC-04** | Form Validation (Controlled Failure + Fix) | Submit empty product form → caught errors → fill valid data → saved | `TC04_controlled_failure.png` | ✅ Automated |
| **TC-05** | User Logout | Click Logout dropdown link → session invalidated, redirect to `/auth/login` | `TC_05_Logout.png` | ✅ Automated |

---

## 3. Controlled Failure Demonstration & Fix (TC-04)

### Step 1: The Controlled Failure
- The test navigates to `/admin/products/new`.
- Clicks "Create Product" with all mandatory fields empty (`name`, `sku`, `price`, `category`).
- System triggers server-side Bean Validation (`@Valid`).
- Form submission fails as expected; page retains values with Bootstrap validation classes (`.is-invalid`) and displays:
  - *"Product name is required"*
  - *"SKU is required"*
  - *"Price is required"*
- Selenium automatically captures the failure screenshot to `target/screenshots/TC04_controlled_failure_validation.png`.

### Step 2: The Controlled Fix
- The test automatically remedies each invalid field with valid test parameters:
  - Name: `"Test Brake Pad"`
  - SKU: `"TBP-SEL001"`
  - Description: `"High performance ceramic brake pads"`
  - Category: `"Brakes"`
  - Price: `"899.00"`
  - Stock: `"25"`
- Submits form again.
- System validates, persists product to database, and redirects with success banner: *"Product 'Test Brake Pad' created successfully."*

---

## 4. How to Run UI Tests Against a Live Instance

1. Start application:
   ```powershell
   .\run-mvn.bat spring-boot:run
   ```
2. Execute Selenium test profile:
   ```powershell
   .\run-mvn.bat test -Pselenium -Dapp.url=http://localhost:8080
   ```
3. Inspect captured screenshots in: `target/screenshots/*.png`

# SDET Take-Home – UI + API Automation Framework

| Part | Application | Tools |
|---|---|---|
| **UI** | [SauceDemo](https://www.saucedemo.com) – demo shop | Selenium 4, TestNG, Page Object Model |
| **API** | [Restful-Booker](https://restful-booker.herokuapp.com/apidoc/index.html) – hotel booking API | REST Assured, TestNG |

**40 tests (17 UI + 23 API), all passing.** Sample report: [`sample-output/`](sample-output).

## How to run

Needs: Java 11+, Maven, Google Chrome.

```bash
mvn clean test                              # all tests
mvn clean test -Dsuite=testng-ui.xml        # UI only
mvn clean test -Dsuite=testng-api.xml       # API only
mvn clean test -Dsuite=testng-smoke.xml     # smoke tests only
mvn clean test -Dtest=CheckoutTest          # one test class
mvn clean test -Dheadless=false             # watch the browser
mvn clean test -Denv=staging                # use config/staging.properties
```

After a run open **`reports/ExtentReport.html`** (HTML report with steps and screenshots).
Logs are in `reports/logs/execution.log`; failure screenshots are in `reports/screenshots/`.

## Project structure

```
src/main/java/com/sdet
├── config/ConfigReader          reads config/qa.properties (or staging), -D overrides
├── driver/DriverManager         starts/stops the browser (one per thread for parallel runs)
├── pages/                       Page Objects
│   ├── BasePage                 common actions with explicit waits + header (cart) actions
│   ├── LoginPage, InventoryPage, CartPage
│   └── CheckoutInformationPage, CheckoutOverviewPage, CheckoutCompletePage
├── api/
│   ├── ApiClient                request spec + response spec (shared settings)
│   ├── BookingApi, AuthApi      reusable API calls
│   └── models/                  POJOs: Booking, BookingDates, AuthRequest
├── reporting/ReportManager      Extent HTML report + step logging
├── listeners/TestListener       logs every test and records pass/fail in the report
├── listeners/RetryAnalyzer      re-runs a failed API test once
└── utils/                       TestDataReader (JSON test data), ScreenshotUtils

src/test/java/com/sdet
├── base/BaseUiTest              open browser / screenshot on failure / close browser
├── base/BaseApiTest             API classes + test data for API tests
├── ui/                          LoginTest, CartTest, CheckoutTest
└── api/                         AuthApiTest, BookingApiTest, BookingNegativeApiTest

src/test/resources
├── config/                      qa.properties, staging.properties
├── testdata/                    ui-testdata.json, api-testdata.json
└── schemas/                     JSON schemas for API responses
```

## Design decisions

**UI**
- **Page Object Model:** one class per page. Tests only call page methods; locators live inside the pages.
- **Locators:** SauceDemo's `data-test` attributes, e.g. `[data-test='checkout']`. They are made for testing,
  so they rarely change.
- **Waits:** `BasePage` waits (explicit wait) before every click, type and read. There is no `Thread.sleep`.
- **Assertions** check business results:
  - the cart contains the right products, prices and quantities
  - item total + 8% tax = total
  - the exact error messages are shown
  - the cart is empty after the order
- **Independent tests:** every test opens its own browser and logs in, so tests can run alone or in parallel.

**API**
- **`ApiClient`** sets up the request spec once (base URL, JSON headers, timeout, logging) and a success
  response spec (status 200, JSON, response time).
- **`BookingApi` / `AuthApi`** hold the reusable calls.
- **Payloads:** a POJO (`Booking`) for normal requests, a `Map` for partial updates, and a raw string for broken JSON.
- **Authentication:** `AuthApi.getToken()` calls `POST /auth`, and the token is sent as a cookie on PUT/PATCH/DELETE.
- **Validation:** status codes, JSON fields (`body("booking.firstname", equalTo(...))`) and JSON Schema.
- **Test data:** every test creates its own booking, so tests don't depend on each other.

**Framework**
- **Config:** settings live in `config/{env}.properties`, and any one can be overridden with `-D`.
- **Test data:** JSON files read with `TestDataReader`. Data-driven tests use TestNG `@DataProvider`.
- **Groups:** `ui`, `api`, `smoke`, `regression`, `negative`.
- **Parallel runs:** 4 threads. `ThreadLocal` gives every thread its own browser and its own report entry.
- **Retry:** only API tests are retried, once, because the public server can be slow.
- **Failure evidence:** a UI failure gets a screenshot in the report; for an API failure the request and
  response are in the log.

## Test coverage

**UI – 17 tests**

| Test | What it checks |
|---|---|
| `CheckoutTest.completePurchase` | **End-to-end:** login → add 2 products → verify cart → checkout → customer details → verify items, payment, shipping, item total, tax, total → finish → verify confirmation |
| `CheckoutTest.checkoutWithMissingDetails` | Missing first name / last name / postal code / all (4 cases) |
| `LoginTest.validLogin` | Products page, 6 products, empty cart |
| `LoginTest.invalidLogin` | Locked user, wrong password, unknown user, empty username, empty password, wrong case (6 cases) |
| `LoginTest.productsPageNeedsLogin` | Products page can't be opened without login |
| `LoginTest.logout` | Logout returns to login page |
| `CartTest` | Cart content (name, price, quantity), remove item, Add/Remove button and badge |

**API – 23 tests**

| Test | What it checks |
|---|---|
| `AuthApiTest` | Valid login returns a token (+ schema). Wrong password, unknown user, missing password and empty body return no token (4 cases) |
| `BookingApiTest` | **POST** create (+ schema), **GET** by id (+ schema, POJO), GET all, **PUT** update + saved, **PATCH** only changes sent fields, **DELETE** then 404 |
| `BookingNegativeApiTest` | Invalid ids 999999999 / 0 / -1 / "abc" → 404. Invalid or empty token on PUT/DELETE → 403. Missing required fields (3 cases). Broken JSON → 400 |

## API defects found

The tests check the API's current behaviour, and each of these is commented in the code:

| Request | Actual | Should be |
|---|---|---|
| `POST /auth` with wrong password | 200 + "Bad credentials" | 401 |
| `POST /booking` with missing fields | 500 | 400 |
| `POST /booking` with `"totalprice": "abc"` | 200, saved as null | 400 |
| `DELETE /booking/{id}` success | 201 | 200 or 204 |

## Assumptions

- **ReqRes was replaced by Restful-Booker.** ReqRes now limits free use to 40 requests per day and returns
  429 after that, and one run of this suite needs about 30. Restful-Booker has no limit and has real login
  and real saved data.
- The demo credentials (`standard_user/secret_sauce`, `admin/password123`) are public. In a real project
  they would come from CI secrets, e.g. `-Dapi.password=...`.
- Both demo sites are public and can be slow. That's why there are explicit waits, a one-time page reload
  in `LoginPage.open()` and one retry for API tests.
- `staging.properties` uses the same URLs. It only shows how a second environment would be added.

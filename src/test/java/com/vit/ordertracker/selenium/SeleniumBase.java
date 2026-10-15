package com.vit.ordertracker.selenium;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Base class for browser tests. The application is started on a random port for the test run.
 * System properties (all optional):
 *   -Dbrowser=chrome|edge   browser to use (default chrome; Selenium Manager downloads Chrome for Testing and its driver)
 *   -Dheadless=false        show the browser window (default: headless)
 *   -Dbase.url=URL          test an already deployed application instead (e.g. http://localhost:8082/order-tracker-dev)
 */
@Tag("selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class SeleniumBase {

    @LocalServerPort
    private int port;

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @RegisterExtension
    final ScreenshotOnFailure screenshots = new ScreenshotOnFailure(() -> driver);

    @BeforeEach
    void startBrowser() {
        baseUrl = System.getProperty("base.url", "http://localhost:" + port);
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean headless = !"false".equalsIgnoreCase(System.getProperty("headless", "true"));
        String[] args = {"--window-size=1280,900", "--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*"};

        if (browser.equals("chrome")) {
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            options.addArguments(args);
            driver = new ChromeDriver(options);
        } else {
            EdgeOptions options = new EdgeOptions();
            if (headless) {
                options.addArguments("--headless=new");
            }
            options.addArguments(args);
            driver = new EdgeDriver(options);
        }
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ---------- helpers ----------

    protected void open(String path) {
        driver.get(baseUrl + path);
    }

    protected void type(By locator, String text) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        field.clear();
        field.sendKeys(text);
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected String text(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    /** Fills and submits the "Add item" form. */
    protected void addItem(String sku, String name, String price, String stock, String reorder) {
        open("/items/new");
        type(By.id("sku"), sku);
        type(By.id("name"), name);
        type(By.id("price"), price);
        type(By.id("stock"), stock);
        type(By.id("reorderLevel"), reorder);
        click(By.id("save-item"));
        // wait for the result: success message, or an error on the form (avoids racing the redirect)
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.id("msg-ok")),
                ExpectedConditions.visibilityOfElementLocated(By.id("msg-error")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".err"))));
    }

    /** Reads the stock of an item from the item list. */
    protected int stockOf(String sku) {
        open("/items?q=" + sku);
        String value = text(By.xpath("//table[@id='items-table']//tr[td[normalize-space()='" + sku + "']]/td[4]"));
        return Integer.parseInt(value.trim());
    }

    /** Fills and submits the "Create order" form. */
    protected void fillOrderForm(String customer, String sku, int quantity) {
        open("/orders/new");
        type(By.id("customerName"), customer);
        Select item = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("itemId"))));
        for (WebElement option : item.getOptions()) {
            if (option.getText().startsWith(sku + " - ")) {
                item.selectByVisibleText(option.getText());
                break;
            }
        }
        type(By.id("quantity"), String.valueOf(quantity));
        click(By.id("place-order"));
        // wait for the result: the new order page, or an error on the form
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlMatches(".*/orders/\\d+$"),
                ExpectedConditions.visibilityOfElementLocated(By.id("msg-error")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".err"))));
    }

    /** Creates an order and returns its ID (read from the order page URL). */
    protected String createOrder(String customer, String sku, int quantity) {
        fillOrderForm(customer, sku, quantity);
        wait.until(ExpectedConditions.urlMatches(".*/orders/\\d+$"));
        String url = driver.getCurrentUrl();
        return url.substring(url.lastIndexOf('/') + 1);
    }
}

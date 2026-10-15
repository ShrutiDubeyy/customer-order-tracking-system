package com.vit.ordertracker.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Five critical user journeys of the Customer Order Tracking System.
 * Every test creates its own data with a unique suffix, so the tests are independent of each other.
 */
class CriticalJourneysTest extends SeleniumBase {

    private final String uid = String.valueOf(System.nanoTime() % 100000000L);

    private String sku(String prefix) {
        return prefix + "-" + uid;
    }

    @Test
    void journey1_addItemAndRejectInvalidData() {
        String sku = sku("SEL1");

        addItem(sku, "Selenium Mouse", "499.00", "10", "3");
        assertEquals("Item saved", text(By.id("msg-ok")));
        assertTrue(text(By.id("items-table")).contains(sku));

        // duplicate SKU is rejected
        addItem(sku, "Another Mouse", "299.00", "5", "1");
        assertTrue(text(By.id("msg-error")).contains("already exists"));

        // negative stock is rejected by validation
        addItem(sku("SEL1B"), "Bad Stock", "100.00", "-5", "1");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.cssSelector(".err"), "Stock cannot be negative"));
    }

    @Test
    void journey2_createOrderReducesStockAndRejectsOversizedOrder() {
        String sku = sku("SEL2");
        String customer = "Selenium User " + uid;
        addItem(sku, "Selenium Keyboard", "999.00", "20", "2");

        String orderId = createOrder(customer, sku, 3);
        assertTrue(text(By.tagName("h1")).contains(orderId));
        assertEquals("PLACED", text(By.id("order-status")));
        assertEquals(17, stockOf(sku));

        // more than the available stock is rejected and stock stays unchanged
        fillOrderForm(customer, sku, 100);
        assertTrue(text(By.id("msg-error")).contains("Insufficient stock"));
        assertEquals(17, stockOf(sku));
    }

    @Test
    void journey3_orderMovesThroughStatusLifecycle() {
        String sku = sku("SEL3");
        addItem(sku, "Selenium Monitor", "7999.00", "10", "2");
        createOrder("Lifecycle " + uid, sku, 1);

        for (String status : new String[] {"PACKED", "SHIPPED", "DELIVERED"}) {
            click(By.id("btn-" + status));
            wait.until(ExpectedConditions.textToBe(By.id("order-status"), status));
        }

        // a delivered order has no further status buttons
        assertTrue(driver.findElements(By.cssSelector("button[id^='btn-']")).isEmpty());
    }

    @Test
    void journey4_trackOrderAndFilterOrders() {
        String sku = sku("SEL4");
        String customer = "Tracker " + uid;
        addItem(sku, "Selenium Headset", "1999.00", "10", "2");
        String orderId = createOrder(customer, sku, 1);

        open("/track?id=" + orderId);
        assertEquals("PLACED", text(By.id("track-status")));

        open("/track?id=999999");
        assertEquals("Order not found", text(By.id("msg-error")));

        open("/orders");
        type(By.id("customer"), customer);
        click(By.id("filter-btn"));
        By orderRows = By.xpath("//table[@id='orders-table']//tr[td]");
        wait.until(ExpectedConditions.numberOfElementsToBe(orderRows, 1));
        List<WebElement> rows = driver.findElements(orderRows);
        assertEquals(1, rows.size());
        assertTrue(rows.get(0).getText().contains(customer));

        new Select(driver.findElement(By.id("status"))).selectByValue("DELIVERED");
        click(By.id("filter-btn"));
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("no-results"))).isDisplayed());
    }

    @Test
    void journey5_lowStockItemAppearsOnAlertsPage() {
        String low = sku("SEL5L");
        String plenty = sku("SEL5N");
        addItem(low, "Low Stock Cable", "199.00", "1", "5");
        addItem(plenty, "Plenty Cable", "199.00", "50", "5");

        open("/alerts");
        String lowStockTable = text(By.id("lowstock-table"));
        assertTrue(lowStockTable.contains(low));
        assertFalse(lowStockTable.contains(plenty));
    }
}

package com.vit.ordertracker.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Deliberately failing test used to demonstrate the failure screenshot mechanism.
 * It is skipped unless you run:  mvn test -Pselenium -Dsimulate.failure=true
 */
@EnabledIfSystemProperty(named = "simulate.failure", matches = "true")
class FailureScreenshotDemoTest extends SeleniumBase {

    @Test
    void deliberatelyFailingAssertion() {
        open("/");
        assertEquals("This title is wrong on purpose", driver.getTitle());
    }
}

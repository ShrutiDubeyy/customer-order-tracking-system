package com.vit.ordertracker.selenium;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * JUnit 5 extension: when a test fails, saves a screenshot of the browser to target/screenshots.
 * It runs right after the test method, before the browser is closed in @AfterEach.
 */
class ScreenshotOnFailure implements AfterTestExecutionCallback {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final Supplier<WebDriver> driverSupplier;

    ScreenshotOnFailure(Supplier<WebDriver> driverSupplier) {
        this.driverSupplier = driverSupplier;
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty()) {
            return;   // test passed - nothing to capture
        }
        try {
            WebDriver driver = driverSupplier.get();
            if (!(driver instanceof TakesScreenshot)) {
                return;
            }
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String fileName = context.getRequiredTestClass().getSimpleName() + "_"
                    + context.getRequiredTestMethod().getName() + "_"
                    + LocalDateTime.now().format(STAMP) + ".png";
            Path file = dir.resolve(fileName);
            Files.write(file, png);
            System.out.println("Failure screenshot saved: " + file.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Could not save failure screenshot: " + e.getMessage());
        }
    }
}

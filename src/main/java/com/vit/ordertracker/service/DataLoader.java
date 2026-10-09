package com.vit.ordertracker.service;

import com.vit.ordertracker.model.CustomerOrder;
import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.model.OrderStatus;
import com.vit.ordertracker.repository.ItemRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Loads small sample data on first start so the application can be demonstrated immediately. */
@Component
public class DataLoader implements CommandLineRunner {

    private final ItemRepository items;
    private final OrderService orders;
    private final JdbcTemplate jdbc;

    public DataLoader(ItemRepository items, OrderService orders, JdbcTemplate jdbc) {
        this.items = items;
        this.orders = orders;
        this.jdbc = jdbc;
    }

    @Override
    public void run(String... args) {
        if (items.count() > 0) {
            return;
        }
        Item laptop = item("SKU-1001", "Laptop 14 inch", "55000.00", 25, 5);
        Item mouse = item("SKU-1002", "Wireless Mouse", "799.00", 60, 10);
        Item keyboard = item("SKU-1003", "Mechanical Keyboard", "2499.00", 4, 10);   // low stock
        Item monitor = item("SKU-1004", "24 inch Monitor", "9999.00", 15, 3);

        orders.create("Aarav Sharma", laptop.getId(), 1);                              // PLACED
        CustomerOrder shipped = orders.create("Priya Nair", monitor.getId(), 2);
        orders.updateStatus(shipped.getId(), OrderStatus.PACKED);
        orders.updateStatus(shipped.getId(), OrderStatus.SHIPPED);                     // SHIPPED
        CustomerOrder delivered = orders.create("Rohan Mehta", keyboard.getId(), 1);
        orders.updateStatus(delivered.getId(), OrderStatus.PACKED);
        orders.updateStatus(delivered.getId(), OrderStatus.SHIPPED);
        orders.updateStatus(delivered.getId(), OrderStatus.DELIVERED);                 // DELIVERED
        CustomerOrder delayed = orders.create("Neha Patil", mouse.getId(), 2);         // made old -> delayed alert
        jdbc.update("update customer_orders set updated_at = ? where id = ?",
                LocalDateTime.now().minusHours(72), delayed.getId());
    }

    private Item item(String sku, String name, String price, int stock, int reorder) {
        Item i = new Item();
        i.setSku(sku);
        i.setName(name);
        i.setPrice(new BigDecimal(price));
        i.setStock(stock);
        i.setReorderLevel(reorder);
        return items.save(i);
    }
}

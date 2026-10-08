package com.vit.ordertracker.web;

import com.vit.ordertracker.model.CustomerOrder;
import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.model.OrderForm;
import com.vit.ordertracker.model.OrderStatus;
import com.vit.ordertracker.service.AlertService;
import com.vit.ordertracker.service.ItemService;
import com.vit.ordertracker.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** JSON REST API (used for testing, and by future clients). */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final ItemService items;
    private final OrderService orders;
    private final AlertService alerts;

    public ApiController(ItemService items, OrderService orders, AlertService alerts) {
        this.items = items;
        this.orders = orders;
        this.alerts = alerts;
    }

    @GetMapping("/items")
    public List<Item> listItems(@RequestParam(required = false) String q) {
        return items.search(q);
    }

    @GetMapping("/items/{id}")
    public Item getItem(@PathVariable Long id) {
        return items.get(id);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@Valid @RequestBody Item item) {
        item.setId(null);
        return items.save(item);
    }

    @PutMapping("/items/{id}")
    public Item updateItem(@PathVariable Long id, @Valid @RequestBody Item item) {
        items.get(id);
        item.setId(id);
        return items.save(item);
    }

    @GetMapping("/orders")
    public List<CustomerOrder> listOrders(@RequestParam(required = false) OrderStatus status,
                                          @RequestParam(required = false) String customer,
                                          @RequestParam(required = false) String item) {
        return orders.search(status, customer, item);
    }

    @GetMapping("/orders/{id}")
    public CustomerOrder getOrder(@PathVariable Long id) {
        return orders.get(id);
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder createOrder(@Valid @RequestBody OrderForm form) {
        return orders.create(form.getCustomerName(), form.getItemId(), form.getQuantity());
    }

    @PatchMapping("/orders/{id}/status")
    public CustomerOrder updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        OrderStatus next = OrderStatus.valueOf(body.getOrDefault("status", "").toUpperCase());
        return orders.updateStatus(id, next);
    }

    @GetMapping("/alerts")
    public Map<String, Object> getAlerts() {
        return Map.of("lowStockItems", alerts.lowStockItems(),
                      "delayedOrders", alerts.delayedOrders(),
                      "delayThresholdHours", alerts.getDelayHours());
    }
}

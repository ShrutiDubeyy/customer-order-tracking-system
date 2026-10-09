package com.vit.ordertracker.service;

import com.vit.ordertracker.model.CustomerOrder;
import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.repository.ItemRepository;
import com.vit.ordertracker.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Exception alerts: low stock items and orders not updated within the delay threshold. */
@Service
public class AlertService {

    private final ItemRepository items;
    private final OrderRepository orders;
    private final long delayHours;

    public AlertService(ItemRepository items, OrderRepository orders,
                        @Value("${app.alerts.delay-hours:48}") long delayHours) {
        this.items = items;
        this.orders = orders;
        this.delayHours = delayHours;
    }

    public List<Item> lowStockItems() {
        return items.findLowStock();
    }

    public List<CustomerOrder> delayedOrders() {
        LocalDateTime limit = LocalDateTime.now().minusHours(delayHours);
        return orders.findAll().stream()
                .filter(o -> o.getStatus().isActive())
                .filter(o -> o.getUpdatedAt() != null && o.getUpdatedAt().isBefore(limit))
                .toList();
    }

    public long getDelayHours() {
        return delayHours;
    }
}

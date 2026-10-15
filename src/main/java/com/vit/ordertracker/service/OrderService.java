package com.vit.ordertracker.service;

import com.vit.ordertracker.model.CustomerOrder;
import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.model.OrderStatus;
import com.vit.ordertracker.repository.ItemRepository;
import com.vit.ordertracker.repository.OrderRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final ItemRepository items;

    public OrderService(OrderRepository orders, ItemRepository items) {
        this.orders = orders;
        this.items = items;
    }

    @Transactional
    public CustomerOrder create(String customerName, Long itemId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        Item item = items.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item " + itemId + " not found"));
        if (quantity > item.getStock()) {
            throw new IllegalArgumentException("Insufficient stock for " + item.getName()
                    + ": requested " + quantity + ", available " + item.getStock());
        }
        item.setStock(item.getStock() - quantity);
        items.save(item);

        CustomerOrder order = new CustomerOrder();
        order.setCustomerName(customerName.trim());
        order.setItem(item);
        order.setQuantity(quantity);
        order.setStatus(OrderStatus.PLACED);
        return orders.save(order);
    }

    @Transactional
    public CustomerOrder updateStatus(Long id, OrderStatus next) {
        CustomerOrder order = get(id);
        if (!order.getStatus().canMoveTo(next)) {
            throw new IllegalStateException("Cannot change status from " + order.getStatus() + " to " + next);
        }
        if (next == OrderStatus.CANCELLED) {
            Item item = order.getItem();
            item.setStock(item.getStock() + order.getQuantity());   // cancelled order returns stock
            items.save(item);
        }
        order.setStatus(next);
        return orders.save(order);
    }

    public CustomerOrder get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }

    public Optional<CustomerOrder> find(Long id) {
        return orders.findById(id);
    }

    /** Optional filters: status, customer name (contains), item name or SKU (contains). */
    public List<CustomerOrder> search(OrderStatus status, String customer, String item) {
        String c = customer == null ? "" : customer.trim().toLowerCase();
        String i = item == null ? "" : item.trim().toLowerCase();
        return orders.findAll().stream()
                .filter(o -> status == null || o.getStatus() == status)
                .filter(o -> c.isEmpty() || o.getCustomerName().toLowerCase().contains(c))
                .filter(o -> i.isEmpty()
                        || o.getItem().getName().toLowerCase().contains(i)
                        || o.getItem().getSku().toLowerCase().contains(i))
                .sorted(Comparator.comparing(CustomerOrder::getId).reversed())
                .toList();
    }

    public long count() {
        return orders.count();
    }
}

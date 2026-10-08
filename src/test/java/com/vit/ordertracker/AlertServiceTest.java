package com.vit.ordertracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import com.vit.ordertracker.model.CustomerOrder;
import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.model.OrderStatus;
import com.vit.ordertracker.repository.ItemRepository;
import com.vit.ordertracker.repository.OrderRepository;
import com.vit.ordertracker.service.AlertService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private ItemRepository items;

    @Mock
    private OrderRepository orders;

    private CustomerOrder order(OrderStatus status, int hoursAgo) {
        CustomerOrder o = new CustomerOrder();
        o.setStatus(status);
        o.setUpdatedAt(LocalDateTime.now().minusHours(hoursAgo));
        return o;
    }

    @Test
    void lowStockItemsComeFromRepository() {
        when(items.findLowStock()).thenReturn(List.of(new Item()));

        AlertService service = new AlertService(items, orders, 48);

        assertEquals(1, service.lowStockItems().size());
    }

    @Test
    void onlyActiveOrdersNotUpdatedForLongAreDelayed() {
        CustomerOrder old = order(OrderStatus.PLACED, 72);
        CustomerOrder fresh = order(OrderStatus.PLACED, 1);
        CustomerOrder delivered = order(OrderStatus.DELIVERED, 100);
        when(orders.findAll()).thenReturn(List.of(old, fresh, delivered));

        List<CustomerOrder> delayed = new AlertService(items, orders, 48).delayedOrders();

        assertEquals(1, delayed.size());
        assertSame(old, delayed.get(0));
    }
}

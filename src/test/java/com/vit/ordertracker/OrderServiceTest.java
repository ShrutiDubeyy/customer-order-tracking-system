package com.vit.ordertracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vit.ordertracker.model.CustomerOrder;
import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.model.OrderStatus;
import com.vit.ordertracker.repository.ItemRepository;
import com.vit.ordertracker.repository.OrderRepository;
import com.vit.ordertracker.service.OrderService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orders;

    @Mock
    private ItemRepository items;

    @InjectMocks
    private OrderService service;

    private Item item(int stock) {
        Item i = new Item();
        i.setId(1L);
        i.setName("Mouse");
        i.setStock(stock);
        return i;
    }

    private CustomerOrder order(Item item, int qty, OrderStatus status) {
        CustomerOrder o = new CustomerOrder();
        o.setId(5L);
        o.setItem(item);
        o.setQuantity(qty);
        o.setStatus(status);
        return o;
    }

    @Test
    void createReducesStockAndSetsPlaced() {
        Item i = item(10);
        when(items.findById(1L)).thenReturn(Optional.of(i));
        when(orders.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        CustomerOrder created = service.create("Asha", 1L, 3);

        assertEquals(7, i.getStock());
        assertEquals(OrderStatus.PLACED, created.getStatus());
    }

    @Test
    void createRejectsQuantityAboveStock() {
        when(items.findById(1L)).thenReturn(Optional.of(item(2)));

        assertThrows(IllegalArgumentException.class, () -> service.create("Asha", 1L, 5));
        verify(orders, never()).save(any(CustomerOrder.class));
    }

    @Test
    void cancellingReturnsStock() {
        Item i = item(7);
        CustomerOrder o = order(i, 3, OrderStatus.PLACED);
        when(orders.findById(5L)).thenReturn(Optional.of(o));
        when(orders.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        service.updateStatus(5L, OrderStatus.CANCELLED);

        assertEquals(10, i.getStock());
        assertEquals(OrderStatus.CANCELLED, o.getStatus());
    }

    @Test
    void rejectsInvalidStatusMove() {
        CustomerOrder o = order(item(5), 1, OrderStatus.PLACED);
        when(orders.findById(5L)).thenReturn(Optional.of(o));

        assertThrows(IllegalStateException.class, () -> service.updateStatus(5L, OrderStatus.DELIVERED));
    }
}

package com.vit.ordertracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vit.ordertracker.model.OrderStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderStatusTest {

    @Test
    void placedCanBePackedOrCancelled() {
        assertTrue(OrderStatus.PLACED.canMoveTo(OrderStatus.PACKED));
        assertTrue(OrderStatus.PLACED.canMoveTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.PLACED.canMoveTo(OrderStatus.DELIVERED));
    }

    @Test
    void shippedCanOnlyBeDelivered() {
        assertEquals(List.of(OrderStatus.DELIVERED), OrderStatus.SHIPPED.nextStatuses());
    }

    @Test
    void deliveredAndCancelledAreFinal() {
        assertTrue(OrderStatus.DELIVERED.nextStatuses().isEmpty());
        assertTrue(OrderStatus.CANCELLED.nextStatuses().isEmpty());
        assertFalse(OrderStatus.DELIVERED.isActive());
    }
}

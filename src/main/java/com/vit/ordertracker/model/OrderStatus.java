package com.vit.ordertracker.model;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {
    PLACED, PACKED, SHIPPED, DELIVERED, CANCELLED;

    /** Allowed life cycle: PLACED -> PACKED -> SHIPPED -> DELIVERED; PLACED/PACKED may be CANCELLED. */
    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case PLACED -> next == PACKED || next == CANCELLED;
            case PACKED -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == DELIVERED;
            default -> false;
        };
    }

    public List<OrderStatus> nextStatuses() {
        return Arrays.stream(values()).filter(this::canMoveTo).toList();
    }

    /** An order is "active" while it is neither delivered nor cancelled. */
    public boolean isActive() {
        return this != DELIVERED && this != CANCELLED;
    }
}

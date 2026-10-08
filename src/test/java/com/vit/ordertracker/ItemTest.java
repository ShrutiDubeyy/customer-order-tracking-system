package com.vit.ordertracker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vit.ordertracker.model.Item;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void itemIsLowStockWhenStockIsAtOrBelowReorderLevel() {
        Item item = new Item();
        item.setReorderLevel(5);

        item.setStock(5);
        assertTrue(item.isLowStock());

        item.setStock(4);
        assertTrue(item.isLowStock());

        item.setStock(6);
        assertFalse(item.isLowStock());
    }
}

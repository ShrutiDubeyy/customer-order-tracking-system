package com.vit.ordertracker.service;

import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.repository.ItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/** Exception alerts. First version: low-stock items only. */
@Service
public class AlertService {

    private final ItemRepository items;

    public AlertService(ItemRepository items) {
        this.items = items;
    }

    public List<Item> lowStockItems() {
        return items.findLowStock();
    }
}

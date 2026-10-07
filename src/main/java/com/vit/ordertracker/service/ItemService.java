package com.vit.ordertracker.service;

import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.repository.ItemRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemRepository repo;

    public ItemService(ItemRepository repo) {
        this.repo = repo;
    }

    public List<Item> search(String q) {
        if (q == null || q.isBlank()) {
            return repo.findAll(Sort.by("name"));
        }
        String term = q.trim();
        return repo.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(term, term);
    }

    public Item get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Item " + id + " not found"));
    }

    public Item save(Item item) {
        item.setSku(item.getSku().trim());
        repo.findBySku(item.getSku()).ifPresent(existing -> {
            if (!existing.getId().equals(item.getId())) {
                throw new IllegalArgumentException("SKU '" + item.getSku() + "' already exists");
            }
        });
        return repo.save(item);
    }

    public long count() {
        return repo.count();
    }
}

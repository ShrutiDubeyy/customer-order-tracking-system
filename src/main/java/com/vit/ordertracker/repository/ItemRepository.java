package com.vit.ordertracker.repository;

import com.vit.ordertracker.model.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findBySku(String sku);

    List<Item> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku);

    @Query("select i from Item i where i.stock <= i.reorderLevel order by i.stock")
    List<Item> findLowStock();
}

package com.vit.ordertracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.repository.ItemRepository;
import com.vit.ordertracker.service.ItemService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository repo;

    @InjectMocks
    private ItemService service;

    @Test
    void rejectsDuplicateSku() {
        Item existing = new Item();
        existing.setId(1L);
        existing.setSku("SKU-1");
        when(repo.findBySku("SKU-1")).thenReturn(Optional.of(existing));

        Item newItem = new Item();
        newItem.setSku(" SKU-1 ");

        assertThrows(IllegalArgumentException.class, () -> service.save(newItem));
        verify(repo, never()).save(any(Item.class));
    }

    @Test
    void allowsEditingTheSameItem() {
        Item existing = new Item();
        existing.setId(1L);
        existing.setSku("SKU-1");
        when(repo.findBySku("SKU-1")).thenReturn(Optional.of(existing));
        when(repo.save(any(Item.class))).thenAnswer(inv -> inv.getArgument(0));

        Item edited = new Item();
        edited.setId(1L);
        edited.setSku("SKU-1");
        edited.setName("Renamed");

        assertEquals("Renamed", service.save(edited).getName());
    }
}

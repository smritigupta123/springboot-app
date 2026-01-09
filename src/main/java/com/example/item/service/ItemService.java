package com.example.item.service;

import com.example.item.model.Item;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ItemService {

    private final Map<String, Item> store = new ConcurrentHashMap<>();

    public Item create(Item item) {
        if (store.containsKey(item.getItemId())) {
            throw new IllegalArgumentException("Item with itemId already exists");
        }
        store.put(item.getItemId(), item);
        return item;
    }

    public Optional<Item> getById(String itemId) {
        return Optional.ofNullable(store.get(itemId));
    }

    public Optional<Item> update(String itemId, Item item) {
        if (!store.containsKey(itemId)) {
            return Optional.empty();
        }
        Item existing = store.get(itemId);
        existing.setName(item.getName());
        existing.setDescription(item.getDescription());
        return Optional.of(existing);
    }

    public boolean delete(String itemId) {
        return store.remove(itemId) != null;
    }
}

package com.demo.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

/** The warehouse, held in memory. */
@Component
public class InventoryStore {

    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public InventoryStore() {
        add("SKU-1001", "Widget", 25);
        add("SKU-1002", "Gadget", 8);
        add("SKU-1003", "Doohickey", 0);
    }

    public Item add(String sku, String name, int quantity) {
        long id = ids.incrementAndGet();
        Item item = new Item(id, sku, name, Math.max(quantity, 0));
        items.put(id, item);
        return item;
    }

    public List<Item> all() {
        List<Item> found = new ArrayList<>(items.values());
        found.sort((left, right) -> Long.compare(left.getId(), right.getId()));
        return found;
    }

    public List<Item> lowStock(int threshold) {
        List<Item> found = new ArrayList<>();
        for (Item item : all()) {
            if (item.getQuantity() < threshold) {
                found.add(item);
            }
        }
        return found;
    }

    public Optional<Item> find(long id) {
        return Optional.ofNullable(items.get(id));
    }

    /** Removes {@code amount} from stock, or returns empty when it cannot. */
    public Optional<Item> reserve(long id, int amount) {
        if (amount < 1) {
            return Optional.empty();
        }
        Item item = items.get(id);
        if (item == null || item.getQuantity() < amount) {
            return Optional.empty();
        }
        item.setQuantity(item.getQuantity() - amount);
        return Optional.of(item);
    }

    public Optional<Item> restock(long id, int amount) {
        if (amount < 1) {
            return Optional.empty();
        }
        Item item = items.get(id);
        if (item == null) {
            return Optional.empty();
        }
        item.setQuantity(item.getQuantity() + amount);
        return Optional.of(item);
    }

    public int size() {
        return items.size();
    }
}

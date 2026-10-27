package com.demo.inventory;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    private final InventoryStore store;

    public InventoryController(InventoryStore store) {
        this.store = store;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "items", store.size());
    }

    @GetMapping("/items")
    public Map<String, List<Item>> items(@RequestParam(required = false) Integer below) {
        if (below != null) {
            return Map.of("items", store.lowStock(below));
        }
        return Map.of("items", store.all());
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<Item> item(@PathVariable long id) {
        return store.find(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/items/{id}/reserve")
    public ResponseEntity<?> reserve(@PathVariable long id, @RequestBody Map<String, Integer> body) {
        int amount = body.getOrDefault("amount", 1);
        return store.reserve(id, amount)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(409)
                        .body(Map.of("error", "not enough stock, or no such item")));
    }

    @PostMapping("/items/{id}/restock")
    public ResponseEntity<?> restock(@PathVariable long id, @RequestBody Map<String, Integer> body) {
        int amount = body.getOrDefault("amount", 0);
        return store.restock(id, amount)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest()
                        .body(Map.of("error", "no such item, or a bad amount")));
    }
}

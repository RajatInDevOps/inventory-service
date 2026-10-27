package com.demo.inventory;

public class Item {
    private final long id;
    private final String sku;
    private final String name;
    private int quantity;

    public Item(long id, String sku, String name, int quantity) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.quantity = quantity;
    }

    public long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    void setQuantity(int quantity) { this.quantity = quantity; }
}

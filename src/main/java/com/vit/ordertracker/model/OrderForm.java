package com.vit.ordertracker.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Data submitted when a new order is created (web form or REST call). */
public class OrderForm {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Please choose an item")
    private Long itemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity = 1;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

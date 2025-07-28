package com.fsoft.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateOrderRequestDTO {
    private Long userId;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String shippingAddress;
    private String note;

    // Constructors
    public CreateOrderRequestDTO() {}
    
    public CreateOrderRequestDTO(Long userId, Long productId, Integer quantity, String shippingAddress) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.shippingAddress = shippingAddress;
    }

    // Getters and Setters
    public Long getUserId() { 
        return userId; 
    }
    
    public void setUserId(Long userId) { 
        this.userId = userId; 
    }
    
    public Long getProductId() { 
        return productId; 
    }
    
    public void setProductId(Long productId) { 
        this.productId = productId; 
    }
    
    public Integer getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
}

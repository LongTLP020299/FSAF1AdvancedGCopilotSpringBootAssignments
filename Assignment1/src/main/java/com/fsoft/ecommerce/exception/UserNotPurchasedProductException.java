package com.fsoft.ecommerce.exception;

public class UserNotPurchasedProductException extends RuntimeException {
    public UserNotPurchasedProductException(String message) {
        super(message);
    }
}

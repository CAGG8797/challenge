package com.challenge.api.exceptions;

public class OutOfStockException extends Exception {
    public OutOfStockException(String productId) {
        super("Product with id " + productId + " is out of stock");
    }
}

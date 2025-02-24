package com.challenge.api.model.dto;

public record OrderItemRequest(String orderId, String productId, int quantity) {
}

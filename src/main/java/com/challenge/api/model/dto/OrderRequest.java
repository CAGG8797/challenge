package com.challenge.api.model.dto;

import java.util.List;

public record OrderRequest(String customerName,
                           List<OrderItemRequest> items) {
}

package com.challenge.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private String id;
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
}

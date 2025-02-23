package com.challenge.api.model.dto;

import java.math.BigDecimal;

public record Product(
        String id,
        String name,
        String description,
        Integer onHand,
        BigDecimal unitPrice
){}

package com.challenge.api.model.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", updatable = false, nullable = false)
    private String id;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private BigInteger quantity;

    @Column(name = "unit_price", nullable = false)
    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable = false)
    private ProductDAO product;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private OrderDAO order;
}

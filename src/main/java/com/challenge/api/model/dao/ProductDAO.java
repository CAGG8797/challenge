package com.challenge.api.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity
@AllArgsConstructor
public class ProductDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", updatable = false, nullable = false)
    private String id;

    @Column(name = "product_name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "product_description", nullable = false, length = 200)
    private String description;

    @Column(name = "on_hand", nullable = false)
    private BigInteger onHand;

    @Column(name = "unit_price", nullable = false, precision = 2)
    private BigDecimal unitPrice;
}

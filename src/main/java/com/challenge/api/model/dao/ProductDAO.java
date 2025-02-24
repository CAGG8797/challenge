package com.challenge.api.model.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@Entity(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class ProductDAO {

    public ProductDAO(String id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", updatable = false, nullable = false)
    private String id;

    @Column(name = "product_name", nullable = false, length = 50)
    @NotNull(message = "Product name is required")
    @NotBlank(message = "Product name is required")
    @Length(min = 3, max = 50, message = "Product name must be between 3 and 50 characters")
    private String name;

    @Column(name = "product_description", nullable = false, length = 200)
    @NotNull(message = "Product description is required")
    @NotBlank(message = "Product description is required")
    @Length(min = 3, max = 200, message = "Product description must be between 3 and 200 characters")
    private String description;

    @Column(name = "on_hand", nullable = false)
    @NotNull(message = "Product on hand is required")
    @Min(value = 0, message = "Product on hand must be greater than or equal to 0")
    private BigInteger onHand;

    @Column(name = "unit_price", nullable = false, precision = 5)
    @NotNull(message = "Product unit price is required")
    @Min(value = 0, message = "Product unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;

    @Column(name = "active", nullable = false)
    private boolean active;
}

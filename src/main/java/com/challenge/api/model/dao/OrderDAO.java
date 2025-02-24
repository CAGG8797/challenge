package com.challenge.api.model.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class OrderDAO {

    public OrderDAO(String id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", updatable = false, nullable = false)
    private String id;

    @Column(name = "customer_name", nullable = false, length = 100)
    @NotNull(message = "Customer name is required")
    @Length(min = 3, max = 100, message = "Customer name must be between 3 and 100 characters")
    private String customerName;

    @Column(name = "order_date", nullable = false)
    @NotNull(message = "Order date is required")
    private LocalDateTime localDateTime;

    @Column(name = "total", nullable = false)
    @NotNull(message = "Total is required")
    private BigDecimal total;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItemDAO> items;
}

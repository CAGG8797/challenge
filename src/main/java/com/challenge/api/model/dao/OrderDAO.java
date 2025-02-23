package com.challenge.api.model.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class OrderDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", updatable = false, nullable = false)
    private String id;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime localDateTime;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItemDAO> items;
}

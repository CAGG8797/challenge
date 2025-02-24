package com.challenge.api.services.impl;

import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dao.OrderItemDAO;
import com.challenge.api.repositories.ExtendedCrudRepository;
import com.challenge.api.services.OrderTotalService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service("orderTotalService")
public class OrderTotalServiceImpl implements OrderTotalService {
    private final ExtendedCrudRepository<OrderDAO, String> repository;

    @Autowired
    public OrderTotalServiceImpl(@Qualifier("ordersRepository") ExtendedCrudRepository<OrderDAO, String> repository) {
        this.repository = repository;
    }

    @Async
    @Override
    @Transactional
    public Future<BigDecimal> calculateTotalAsync(String orderId) {
        synchronized (this) {
            return repository.findById(orderId)
                    .map(order -> {
                        BigDecimal total = order.getItems().stream()
                                .filter(OrderItemDAO::isActive)
                                .map(item -> item.getProduct().getUnitPrice().multiply(new BigDecimal(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                        order.setTotal(total);
                        repository.save(order);
                        return CompletableFuture.completedFuture(total);
                    })
                    .orElseGet(() -> CompletableFuture.completedFuture(BigDecimal.ZERO));
        }
    }
}

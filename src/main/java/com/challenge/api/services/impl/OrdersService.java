package com.challenge.api.services.impl;

import com.challenge.api.MapperUtils;
import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.model.dto.OrderItemResponse;
import com.challenge.api.model.dto.OrderRequest;
import com.challenge.api.model.dto.OrderResponse;
import com.challenge.api.repositories.ExtendedCrudRepository;
import com.challenge.api.services.CrudService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service("ordersService")
public class OrdersService implements CrudService<OrderRequest, OrderResponse, String> {

    private final ExtendedCrudRepository<OrderDAO, String> repository;
    private final CrudService<OrderItemRequest, OrderItemResponse, String> orderItemsService;

    @Autowired
    public OrdersService(@Qualifier("ordersRepository") ExtendedCrudRepository<OrderDAO, String> repository,
                         @Qualifier("orderItemsService") CrudService<OrderItemRequest, OrderItemResponse, String> orderItemsService) {
        this.repository = repository;
        this.orderItemsService = orderItemsService;
    }

    @Override
    public Page<OrderResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(MapperUtils::map);
    }

    @Override
    public OrderResponse getById(String id) {
        return MapperUtils.map(getOrderFromDatabase(id));
    }

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("OrderRequest cannot be null");
        }

        final OrderDAO orderDAO = repository.save(new OrderDAO(null, request.customerName(),
                LocalDateTime.now(), BigDecimal.ZERO, true, new LinkedList<>()));

        final List<OrderItemResponse> orderItemResponses = request.items()
                .stream()
                .map(item -> {
                    OrderItemRequest orderItemRequest = new OrderItemRequest(orderDAO.getId(),
                            item.productId(), item.quantity());

                    try {
                        OrderItemResponse orderItemResponse = orderItemsService.create(orderItemRequest);
                        BigDecimal totalOrderItem = orderItemResponse.getPrice().multiply(new BigDecimal(orderItemResponse.getQuantity()));
                        // Update total order
                        orderDAO.setTotal(orderDAO.getTotal().add(totalOrderItem));
                        return orderItemResponse;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        //Update the total in the database
        repository.save(orderDAO);

        OrderResponse response = MapperUtils.map(orderDAO);
        response.setItems(orderItemResponses);
        return response;
    }

    @Override
    @Transactional
    public OrderResponse update(String s, OrderRequest request) throws Exception {
        return null;
    }

    @Override
    @Transactional
    public void delete(String s) {

    }

    private OrderDAO getOrderFromDatabase(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order does not exist"));
    }
}

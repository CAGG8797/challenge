package com.challenge.api.services.impl;

import com.challenge.api.utils.MapperUtils;
import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dao.OrderItemDAO;
import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.model.dto.OrderItemResponse;
import com.challenge.api.model.dto.OrderRequest;
import com.challenge.api.model.dto.OrderResponse;
import com.challenge.api.repositories.ExtendedCrudRepository;
import com.challenge.api.services.CrudService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Map;
import java.util.stream.Collectors;

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

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items are required");
        }

        final OrderDAO orderDAO = repository.save(new OrderDAO(null, request.getCustomerName(),
                LocalDateTime.now(), BigDecimal.ZERO, true, new LinkedList<>()));

        final List<OrderItemResponse> orderItemResponses = request.getItems()
                .stream()
                .map(item -> {
                    OrderItemRequest orderItemRequest = new OrderItemRequest(orderDAO.getId(),
                            item.getProductId(), item.getQuantity());

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
    @Transactional(rollbackOn = Exception.class)
    public OrderResponse update(String id, OrderRequest request) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("OrderRequest cannot be null");
        }

        OrderDAO orderDAO = getOrderFromDatabase(id);
        orderDAO.setCustomerName(request.getCustomerName());

        Map<String, OrderItemDAO> orderItemsById = orderDAO.getItems().stream()
                .collect(Collectors.toMap(OrderItemDAO::getId, item -> item));

        for(OrderItemRequest orderItemRequest : request.getItems()) {
            orderItemRequest.setOrderId(id);
            if (StringUtils.hasText(id)) {
                orderItemsService.update(id, orderItemRequest);
            } else {
                orderItemsService.create(orderItemRequest);
            }
            orderItemsById.remove(id);
        }

        for (String orderItemId: orderItemsById.keySet()) {
            orderItemsService.delete(orderItemId);
        }

        return MapperUtils.map(repository.save(orderDAO));
    }

    @Override
    @Transactional
    public void delete(String id) throws Exception {
        for (OrderItemDAO item : getOrderFromDatabase(id).getItems()) {
            orderItemsService.delete(item.getId());
        }
        repository.softDeleteById(id);
    }

    private OrderDAO getOrderFromDatabase(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }
}

package com.challenge.api.services.impl;

import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.model.dto.OrderItemResponse;
import com.challenge.api.services.CrudService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service("orderItemsService")
public class OrderItemsService implements CrudService<OrderItemRequest, OrderItemResponse, String> {

    @Override
    public Page<OrderItemResponse> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public OrderItemResponse getById(String s) {
        return null;
    }

    @Override
    @Transactional
    public OrderItemResponse create(OrderItemRequest request) throws Exception {
        return null;
    }

    @Override
    @Transactional
    public OrderItemResponse update(String s, OrderItemRequest request) throws Exception {
        return null;
    }

    @Override
    @Transactional
    public void delete(String s) {

    }
}

package com.challenge.api.services.impl;

import com.challenge.api.model.dto.OrderRequest;
import com.challenge.api.model.dto.OrderResponse;
import com.challenge.api.services.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class OrdersService implements CrudService<OrderRequest, OrderResponse, String> {

    @Override
    public Page<OrderResponse> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public OrderResponse getById(String s) {
        return null;
    }

    @Override
    public OrderResponse create(OrderRequest request) throws Exception {
        return null;
    }

    @Override
    public OrderResponse update(String s, OrderRequest request) throws Exception {
        return null;
    }

    @Override
    public void delete(String s) {

    }
}

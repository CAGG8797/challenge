package com.challenge.api.controller;

import com.challenge.api.model.dto.OrderRequest;
import com.challenge.api.model.dto.OrderResponse;
import com.challenge.api.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CrudService<OrderRequest, OrderResponse, String> ordersService;

    @Autowired
    public OrderController(@Qualifier("ordersService") CrudService<OrderRequest, OrderResponse, String> ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping
    public Page<OrderResponse> getAll(Pageable pageable) {
        return ordersService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable String id) {
        return ordersService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody OrderRequest orderRequest) throws Exception {
        return ordersService.create(orderRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse update(@PathVariable String id, @RequestBody OrderRequest orderRequest) throws Exception {
        return ordersService.update(id, orderRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        ordersService.delete(id);
    }
}

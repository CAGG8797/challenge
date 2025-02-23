package com.challenge.api.controller;

import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.model.dto.OrderItemResponse;
import com.challenge.api.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-items")
public class OrderItemsController {

    private final CrudService<OrderItemRequest, OrderItemResponse, String> orderItemsService;
    
    @Autowired
    public OrderItemsController(@Qualifier("orderItemsService") CrudService<OrderItemRequest, OrderItemResponse, String> orderItemsService) {
        this.orderItemsService = orderItemsService;
    }


    @GetMapping
    public Page<OrderItemResponse> getAll(Pageable pageable) {
        return orderItemsService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public OrderItemResponse getById(@PathVariable String id) {
        return orderItemsService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponse create(@RequestBody OrderItemRequest orderItemRequest) throws Exception {
        return orderItemsService.create(orderItemRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponse update(@PathVariable String id, @RequestBody OrderItemRequest orderItemRequest) throws Exception {
        return orderItemsService.update(id, orderItemRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        orderItemsService.delete(id);
    }
}

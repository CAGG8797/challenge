package com.challenge.api.controller;

import com.challenge.api.model.dto.APIErrorResponse;
import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.model.dto.OrderItemResponse;
import com.challenge.api.services.CrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "Get all order items", description = "Retrieve a paginated list of all order items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved list",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @GetMapping
    public Page<OrderItemResponse> getAll(@ParameterObject Pageable pageable) {
        return orderItemsService.getAll(pageable);
    }

    @Operation(summary = "Get order item by ID", description = "Retrieve an order item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved order item",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderItemResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Order item not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public OrderItemResponse getById(@PathVariable String id) {
        return orderItemsService.getById(id);
    }

    @Operation(summary = "Create a new order item", description = "Create a new order item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Order item created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderItemResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponse create(@RequestBody OrderItemRequest orderItemRequest) throws Exception {
        return orderItemsService.create(orderItemRequest);
    }

    @Operation(summary = "Update an order item", description = "Update an existing order item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Order item updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderItemResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Order item not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderItemResponse update(@PathVariable String id, @RequestBody OrderItemRequest orderItemRequest) throws Exception {
        return orderItemsService.update(id, orderItemRequest);
    }

    @Operation(summary = "Delete an order item", description = "Delete an order item by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Order item deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Order item not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) throws Exception {
        orderItemsService.delete(id);
    }
}
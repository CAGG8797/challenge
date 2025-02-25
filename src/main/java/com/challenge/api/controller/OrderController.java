package com.challenge.api.controller;

import com.challenge.api.model.dto.APIErrorResponse;
import com.challenge.api.model.dto.OrderRequest;
import com.challenge.api.model.dto.OrderResponse;
import com.challenge.api.services.CrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all orders", description = "Retrieve a paginated list of all orders")
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
    public Page<OrderResponse> getAll(Pageable pageable) {
        return ordersService.getAll(pageable);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve an order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved order",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable String id) {
        return ordersService.getById(id);
    }

    @Operation(summary = "Create a new order", description = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Order created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody OrderRequest orderRequest) throws Exception {
        return ordersService.create(orderRequest);
    }

    @Operation(summary = "Update an order", description = "Update an existing order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Order updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse update(@PathVariable String id, @RequestBody OrderRequest orderRequest) throws Exception {
        return ordersService.update(id, orderRequest);
    }

    @Operation(summary = "Delete an order", description = "Delete an order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Order not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) throws Exception {
        ordersService.delete(id);
    }
}

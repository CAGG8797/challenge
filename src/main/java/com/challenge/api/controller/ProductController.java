package com.challenge.api.controller;

import com.challenge.api.model.dto.APIErrorResponse;
import com.challenge.api.model.dto.Product;
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
@RequestMapping("/products")
public class ProductController {
    private final CrudService<Product, Product, String> productService;

    @Autowired
    ProductController(@Qualifier("productsService") CrudService<Product, Product, String> productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Retrieve a paginated list of all products")
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
    public Page<Product> getAll(@ParameterObject Pageable pageable) {
        return productService.getAll(pageable);
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved product",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Product getById(@PathVariable String id) {
        return productService.getById(id);
    }

    @Operation(summary = "Create a new product", description = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Product created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) throws Exception {
        return productService.create(product);
    }

    @Operation(summary = "Update a product", description = "Update an existing product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Product updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Product update(@PathVariable String id, @RequestBody Product product) throws Exception {
        return productService.update(id, product);
    }

    @Operation(summary = "Delete a product", description = "Delete a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404",
                    description = "Product not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) throws Exception {
        productService.delete(id);
    }
}

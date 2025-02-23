package com.challenge.api.controller;

import com.challenge.api.model.dto.ProductDTO;
import com.challenge.api.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final CrudService<ProductDTO, String> productService;

    @Autowired
    ProductController(@Qualifier("productService") CrudService<ProductDTO, String> productService) {
        this.productService = productService;
    }


    @GetMapping
    public Page<ProductDTO> getAll(Pageable pageable) {
        return productService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable String id) {
        return productService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(@RequestBody ProductDTO productDTO) throws Exception {
        return productService.create(productDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO update(@PathVariable String id, @RequestBody ProductDTO productDTO) throws Exception {
        return productService.update(productDTO, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }
}

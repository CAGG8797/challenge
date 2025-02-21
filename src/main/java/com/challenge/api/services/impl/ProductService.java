package com.challenge.api.services.impl;

import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.ProductDTO;
import com.challenge.api.repositories.ExtendedCrudRepository;
import com.challenge.api.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service(value = "productService")
public class ProductService implements CrudService<ProductDTO, String> {

    private ExtendedCrudRepository<ProductDAO, String> repository;

    @Autowired
    public ProductService(@Qualifier("productsRepository") ExtendedCrudRepository<ProductDAO, String> repository) {
        this.repository = repository;
    }

    @Override
    public Page<ProductDTO> getAll(Pageable pageable) {
        return null;
    }

    @Override
    public ProductDTO getById(String s) {
        return null;
    }

    @Override
    public ProductDTO create(ProductDTO productDTO) {
        return null;
    }

    @Override
    public ProductDTO update(ProductDTO productDTO, String s) {
        return null;
    }

    @Override
    public void delete(String s) {

    }
}

package com.challenge.api.services.impl;

import com.challenge.api.MapperUtils;
import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.Product;
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

import java.math.BigInteger;

@Service(value = "productsService")
public class ProductService implements CrudService<Product, Product, String> {

    private final ExtendedCrudRepository<ProductDAO, String> repository;

    @Autowired
    public ProductService(@Qualifier("productsRepository") ExtendedCrudRepository<ProductDAO, String> repository) {
        this.repository = repository;
    }

    @Override
    public Page<Product> getAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }

        return repository.findAll(pageable)
                .map(ProductService::mapToDTO);
    }

    @Override
    public Product getById(String id) {
        return mapToDTO(getProductFromDatabase(id));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Product create(Product product) throws Exception {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        ProductDAO productDAO = MapperUtils.map(product);
        productDAO.setId(null);
        productDAO = repository.saveAndFlush(productDAO);

        return mapToDTO(productDAO);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Product update(String id, Product product) throws Exception {
        if (product == null) {
            throw new IllegalArgumentException("Product to update cannot be null");
        }

        ProductDAO productDAO = getProductFromDatabase(id);
        productDAO.setName(product.getName());
        productDAO.setDescription(product.getDescription());
        productDAO.setOnHand(BigInteger.valueOf(product.getOnHand() != null ? product.getOnHand() : 0));
        productDAO.setUnitPrice(product.getUnitPrice());
        productDAO = repository.saveAndFlush(productDAO);

        return mapToDTO(productDAO);

    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(String id) {
        //Validate the product exists and the id is valid
        getProductFromDatabase(id);
        repository.softDeleteById(id);
    }

    private ProductDAO getProductFromDatabase(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Product id cannot be null or empty");
        }

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product with id " + id + " not found"));
    }

    private static Product mapToDTO(ProductDAO productDAO) {
        return new Product(
                productDAO.getId(),
                productDAO.getName(),
                productDAO.getDescription(),
                productDAO.getOnHand().intValue(),
                productDAO.getUnitPrice()
        );
    }
}

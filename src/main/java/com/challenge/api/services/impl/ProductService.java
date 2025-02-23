package com.challenge.api.services.impl;

import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.Product;
import com.challenge.api.repositories.ExtendedCrudRepository;
import com.challenge.api.services.CrudService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;

@Service(value = "productService")
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

        try {
            ProductDAO productDAO = mapToDAO(product);
            productDAO.setId(null);
            productDAO = repository.saveAndFlush(productDAO);
            return mapToDTO(productDAO);
        } catch (ConstraintViolationException e) {
            String message = e.getConstraintViolations().iterator().next().getMessage();
            throw new Exception(message);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Product update(String id, Product product)  throws Exception {
        if (product == null) {
            throw new IllegalArgumentException("Product to update cannot be null");
        }

        try {
            ProductDAO productDAO = getProductFromDatabase(id);

            productDAO.setName(product.name());
            productDAO.setDescription(product.description());
            productDAO.setOnHand(BigInteger.valueOf(product.onHand() != null ? product.onHand() : 0));
            productDAO.setUnitPrice(product.unitPrice());

            productDAO = repository.saveAndFlush(productDAO);

            return mapToDTO(productDAO);
        } catch (ConstraintViolationException e) {
            String message = e.getConstraintViolations().iterator().next().getMessage();
            throw new Exception(message);
        }
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

    private static ProductDAO mapToDAO(Product product) {
        return new ProductDAO(
                product.id(),
                product.name(),
                product.description(),
                BigInteger.valueOf(product.onHand() != null ? product.onHand() : 0),
                product.unitPrice(),
                true
        );
    }
}

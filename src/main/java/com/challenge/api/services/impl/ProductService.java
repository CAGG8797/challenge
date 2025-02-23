package com.challenge.api.services.impl;

import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.ProductDTO;
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
public class ProductService implements CrudService<ProductDTO, String> {

    private final ExtendedCrudRepository<ProductDAO, String> repository;

    @Autowired
    public ProductService(@Qualifier("productsRepository") ExtendedCrudRepository<ProductDAO, String> repository) {
        this.repository = repository;
    }

    @Override
    public Page<ProductDTO> getAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("PageRequest cannot be null");
        }

        return repository.findAll(pageable)
                .map(ProductService::mapToDTO);
    }

    @Override
    public ProductDTO getById(String id) {
        return mapToDTO(getProductFromDatabase(id));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ProductDTO create(ProductDTO productDTO) throws Exception {
        if (productDTO == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        try {
            ProductDAO productDAO = mapToDAO(productDTO);
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
    public ProductDTO update(ProductDTO productDTO, String id)  throws Exception {
        if (productDTO == null) {
            throw new IllegalArgumentException("Product to update cannot be null");
        }

        try {
            ProductDAO productDAO = getProductFromDatabase(id);

            productDAO.setName(productDTO.name());
            productDAO.setDescription(productDTO.description());
            productDAO.setOnHand(BigInteger.valueOf(productDTO.onHand() != null ? productDTO.onHand() : 0));
            productDAO.setUnitPrice(productDTO.unitPrice());

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

    private static ProductDTO mapToDTO(ProductDAO productDAO) {
        return new ProductDTO(
                productDAO.getId(),
                productDAO.getName(),
                productDAO.getDescription(),
                productDAO.getOnHand().intValue(),
                productDAO.getUnitPrice()
        );
    }

    private static ProductDAO mapToDAO(ProductDTO productDTO) {
        return new ProductDAO(
                productDTO.id(),
                productDTO.name(),
                productDTO.description(),
                BigInteger.valueOf(productDTO.onHand() != null ? productDTO.onHand() : 0),
                productDTO.unitPrice(),
                true
        );
    }
}

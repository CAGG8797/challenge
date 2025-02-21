package com.challenge.api.repositories;

import com.challenge.api.model.dao.ProductDAO;
import org.springframework.stereotype.Repository;

@Repository(value = "productsRepository")
public interface ProductsRepository extends ExtendedCrudRepository<ProductDAO, String> {
}

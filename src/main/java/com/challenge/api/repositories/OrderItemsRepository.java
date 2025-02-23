package com.challenge.api.repositories;

import com.challenge.api.model.dao.OrderItemDAO;
import org.springframework.stereotype.Repository;

@Repository(value = "orderItemsRepository")
public interface OrderItemsRepository extends ExtendedCrudRepository<OrderItemDAO, String> {
}

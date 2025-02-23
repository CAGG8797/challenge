package com.challenge.api.repositories;

import com.challenge.api.model.dao.OrderDAO;
import org.springframework.stereotype.Repository;

@Repository(value = "ordersRepository")
public interface OrdersRepository extends ExtendedCrudRepository<OrderDAO, String> {
}

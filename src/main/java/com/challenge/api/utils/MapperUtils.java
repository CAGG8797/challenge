package com.challenge.api.utils;

import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dao.OrderItemDAO;
import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.OrderItemResponse;
import com.challenge.api.model.dto.OrderResponse;
import com.challenge.api.model.dto.Product;
import jakarta.transaction.Transactional;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;

@UtilityClass
public class MapperUtils {

    @Transactional
    public static OrderResponse map(OrderDAO order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setTotal(order.getTotal().setScale(2));
        orderResponse.setCustomerName(order.getCustomerName());

        orderResponse.setItems(order.getItems()
                .stream()
                .filter(OrderItemDAO::isActive)
                .map(MapperUtils::map).toList());

        return orderResponse;
    }

    @Transactional
    public static OrderItemResponse map(OrderItemDAO orderItem) {
        OrderItemResponse orderItemResponse = new OrderItemResponse();
        orderItemResponse.setId(orderItem.getId());
        orderItemResponse.setPrice(orderItem.getUnitPrice());
        orderItemResponse.setProductId(orderItem.getProduct().getId());
        orderItemResponse.setProductName(orderItem.getProduct().getName());
        orderItemResponse.setQuantity(orderItem.getQuantity().intValue());
        return orderItemResponse;
    }

    public static ProductDAO map(Product product) {
        return new ProductDAO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                BigInteger.valueOf(product.getOnHand() != null ? product.getOnHand() : 0),
                product.getUnitPrice(),
                true
        );
    }

    public static Product map(ProductDAO productDAO) {
        return new Product(
                productDAO.getId(),
                productDAO.getName(),
                productDAO.getDescription(),
                productDAO.getOnHand().intValue(),
                productDAO.getUnitPrice()
        );
    }
}

package com.challenge.api.services.impl;

import com.challenge.api.utils.MapperUtils;
import com.challenge.api.exceptions.OutOfStockException;
import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dao.OrderItemDAO;
import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.model.dto.OrderItemResponse;
import com.challenge.api.model.dto.Product;
import com.challenge.api.repositories.ExtendedCrudRepository;
import com.challenge.api.services.CrudService;
import com.challenge.api.services.OrderTotalService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;

@Service("orderItemsService")
public class OrderItemsService implements CrudService<OrderItemRequest, OrderItemResponse, String> {

    private final ExtendedCrudRepository<OrderItemDAO, String> repository;
    private final ExtendedCrudRepository<OrderDAO, String> ordersRepository;
    private final CrudService<Product, Product, String> productService;
    private final OrderTotalService orderTotalService;

    @Autowired
    public OrderItemsService(@Qualifier("orderItemsRepository") ExtendedCrudRepository<OrderItemDAO, String> repository,
                             @Qualifier("ordersRepository") ExtendedCrudRepository<OrderDAO, String> ordersRepository,
                             @Qualifier("productsService") CrudService<Product, Product, String> productService,
                             @Qualifier("orderTotalService") OrderTotalService orderTotalService) {
        this.repository = repository;
        this.ordersRepository = ordersRepository;
        this.productService = productService;
        this.orderTotalService = orderTotalService;
    }

    @Override
    public Page<OrderItemResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(MapperUtils::map);
    }

    @Override
    public OrderItemResponse getById(String id) {
        return MapperUtils.map(getOrderItemFromDatabase(id));
    }

    @Override
    @Transactional(rollbackOn = OutOfStockException.class)
    public OrderItemResponse create(OrderItemRequest request) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("OrderItemRequest cannot be null");
        }

        if (!StringUtils.hasText(request.getOrderId())) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }

        if (!ordersRepository.existsById(request.getOrderId())) {
            throw new EntityNotFoundException("Order does not exist");
        }

        Product product = productService.getById(request.getProductId());

        if (product.getOnHand() < request.getQuantity()) {
            throw new OutOfStockException(request.getProductId());
        }

        OrderItemDAO dao = new OrderItemDAO();
        dao.setOrder(new OrderDAO(request.getOrderId()));
        dao.setProduct(MapperUtils.map(product));
        dao.setQuantity(BigInteger.valueOf(request.getQuantity()));
        dao.setActive(true);
        dao.setUnitPrice(product.getUnitPrice());

        product.setOnHand(product.getOnHand() - request.getQuantity());
        productService.update(request.getProductId(), product);

        dao = repository.saveAndFlush(dao);
        orderTotalService.calculateTotalAsync(request.getOrderId());
        return MapperUtils.map(dao);
    }

    @Override
    @Transactional(rollbackOn = OutOfStockException.class)
    public OrderItemResponse update(String id, OrderItemRequest request) throws Exception {
        String  oldOrderId;
        Integer oldQuantity;
        Product oldProduct;

        if (request == null) {
            throw new IllegalArgumentException("OrderItemRequest cannot be null");
        }

        OrderItemDAO existingOrderItem = getOrderItemFromDatabase(id);

        if (!StringUtils.hasText(request.getOrderId()) || !ordersRepository.existsById(request.getOrderId())) {
            throw new EntityNotFoundException("Order does not exist");
        }

        oldOrderId  = existingOrderItem.getOrder().getId();
        oldQuantity = existingOrderItem.getQuantity().intValue();
        oldProduct  = MapperUtils.map(existingOrderItem.getProduct());
        oldProduct.setOnHand(oldProduct.getOnHand() + oldQuantity);

        productService.update(oldProduct.getId(), oldProduct);

        Product product = productService.getById(request.getProductId());

        if (product.getOnHand() < request.getQuantity()) {
            throw new OutOfStockException(request.getProductId());
        }

        product.setOnHand(product.getOnHand() - request.getQuantity());
        productService.update(product.getId(), product);

        existingOrderItem.setOrder(new OrderDAO(request.getOrderId()));
        existingOrderItem.setProduct(MapperUtils.map(product));
        existingOrderItem.setQuantity(BigInteger.valueOf(request.getQuantity()));
        existingOrderItem.setActive(true);
        existingOrderItem.setUnitPrice(product.getUnitPrice());

        repository.saveAndFlush(existingOrderItem);

        orderTotalService.calculateTotalAsync(request.getOrderId());
        orderTotalService.calculateTotalAsync(oldOrderId);

        return MapperUtils.map(existingOrderItem);
    }

    @Override
    @Transactional
    public void delete(String id) throws Exception {
        OrderItemDAO orderItem = getOrderItemFromDatabase(id);
        repository.softDeleteById(id);

        Product product = productService.getById(orderItem.getProduct().getId());
        product.setOnHand(product.getOnHand() + orderItem.getQuantity().intValue());
        productService.update(product.getId(), product);

        orderTotalService.calculateTotalAsync(orderItem.getOrder().getId());
    }

    private OrderItemDAO getOrderItemFromDatabase(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException("Order Item ID cannot be null or empty");
        }

        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order Item does not exist"));
    }
}

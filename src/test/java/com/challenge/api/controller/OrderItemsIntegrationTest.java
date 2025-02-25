package com.challenge.api.controller;

import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dao.OrderItemDAO;
import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.repositories.OrdersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderItemsIntegrationTest extends IntegrationTestsBase {
    private static String orderId;
    
    @BeforeAll
    public static void setup(@Autowired OrdersRepository ordersRepository) {
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.setCustomerName("Customer 1");
        orderDAO.setActive(true);
        orderDAO.setLocalDateTime(LocalDateTime.now());
        orderDAO.setItems(List.of(new OrderItemDAO(null, BigInteger.ONE, BigDecimal.valueOf(10.00), true, new ProductDAO("id_1"), orderDAO)));
        orderDAO.setTotal(BigDecimal.valueOf(10.00));
        orderId = ordersRepository.save(orderDAO).getId();
    }
    
    @Test
    public void createOrderItemSuccessfully() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest(orderId, "id_1", 1);

        MvcResult result = mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.not(Matchers.blankOrNullString())))
                .andExpect(jsonPath("$.productName", Matchers.is("Product 1")))
                .andExpect(jsonPath("$.productId", Matchers.is("id_1")))
                .andExpect(jsonPath("$.price", Matchers.is(10)))
                .andExpect(jsonPath("$.quantity", Matchers.is(1)))
                .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        // Wait for async total calculation to complete
        Thread.sleep(1000);

        OrderDAO orderDAO = ordersRepository.findById(orderId).get();
        assertEquals(20, orderDAO.getTotal().intValue());
        assertTrue(orderItemsRepository.existsById(id));
    }

    @Test
    public void createOrderItemWithInvalidProductId() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest(orderId, "invalid-product-id", 1);

        mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void createOrderItemWithNegativeQuantity() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest(orderId, "id_1", -1);

        mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createOrderItemWithZeroQuantity() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest(orderId, "id_1", 0);

        mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createOrderItemWithNonExistentOrderId() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest("non-existent-order-id", "id_1", 1);

        mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

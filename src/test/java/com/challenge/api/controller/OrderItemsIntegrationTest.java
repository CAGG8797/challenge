package com.challenge.api.controller;

import com.challenge.api.ApiApplication;
import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.repositories.OrderItemsRepository;
import com.challenge.api.repositories.OrdersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ApiApplication.class)
@AutoConfigureMockMvc
public class OrderItemsIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Test
    public void createOrderItemSuccessfully() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest("2dddbca6-2b3f-49fe-8178-04bc5900144c", "b591a459-2e56-4e6d-9576-5c479c419dc8", 1);

        MvcResult result = mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.not(Matchers.blankOrNullString())))
                .andExpect(jsonPath("$.productName", Matchers.is("Product 1")))
                .andExpect(jsonPath("$.productId", Matchers.is("b591a459-2e56-4e6d-9576-5c479c419dc8")))
                .andExpect(jsonPath("$.price", Matchers.is(10)))
                .andExpect(jsonPath("$.quantity", Matchers.is(1)))
                .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        OrderDAO orderDAO = ordersRepository.findById("2dddbca6-2b3f-49fe-8178-04bc5900144c").get();
        assertEquals(10, orderDAO.getTotal().intValue());
        assertTrue(orderItemsRepository.existsById(id));
    }

    @Test
    public void createOrderItemWithInvalidProductId() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest("2dddbca6-2b3f-49fe-8178-04bc5900144c", "invalid-product-id", 1);

        mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void createOrderItemWithNegativeQuantity() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest("2dddbca6-2b3f-49fe-8178-04bc5900144c", "b591a459-2e56-4e6d-9576-5c479c419dc8", -1);

        mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createOrderItemWithZeroQuantity() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest("2dddbca6-2b3f-49fe-8178-04bc5900144c", "b591a459-2e56-4e6d-9576-5c479c419dc8", 0);

        mvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderItemRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createOrderItemWithNonExistentOrderId() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest("non-existent-order-id", "b591a459-2e56-4e6d-9576-5c479c419dc8", 1);

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

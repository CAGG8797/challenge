package com.challenge.api.controller;

import com.challenge.api.model.dao.OrderDAO;
import com.challenge.api.model.dao.OrderItemDAO;
import com.challenge.api.model.dao.ProductDAO;
import com.challenge.api.model.dto.OrderItemRequest;
import com.challenge.api.model.dto.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrdersIntegrationTest extends IntegrationTestsBase {
    private static final String PATH = "/orders";
    private static final String CUSTOMER_NAME = "Customer 1";

    @Test
    public void getOrdersWhenNoOrdersInDatabase() throws Exception {
        orderItemsRepository.deleteAll();
        ordersRepository.deleteAll();
        mvc.perform(get(PATH).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    public void getOrdersWhenOrdersInDatabase() throws Exception {
        orderItemsRepository.deleteAll();
        ordersRepository.deleteAll();
        insertOrdersInDatabase(10);

        mvc.perform(get(PATH).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()", Matchers.is(10)));
    }

    @Test
    public void getOrderByIdSuccessfully() throws Exception {
        OrderDAO orderDAO = insertNewOrderInDatabase();

        mvc.perform(get(PATH + "/" + orderDAO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(orderDAO.getId())))
                .andExpect(jsonPath("$.customerName", Matchers.is(CUSTOMER_NAME)))
                .andExpect(jsonPath("$.total", Matchers.is(10.00)))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()", Matchers.is(1)));
    }

    @Test
    public void getOrderByIdWhenOrderDoesNotExist() throws Exception {
        mvc.perform(get(PATH + "/invalid-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]", Matchers.is("Order not found")));
    }

    @ParameterizedTest(name = "Create order with {0} items")
    @MethodSource("getOrderItemsRequest")
    public void createOrderSuccessfully(List<OrderItemRequest> orderItems, BigDecimal total) throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerName(CUSTOMER_NAME);
        orderRequest.setItems(orderItems);

        MvcResult result = mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.not(Matchers.blankOrNullString())))
                .andExpect(jsonPath("$.customerName", Matchers.is(CUSTOMER_NAME)))
                .andExpect(jsonPath("$.total", Matchers.is(total.doubleValue())))
                .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        Optional<OrderDAO> orderDAO = ordersRepository.findById(id);

        assertTrue(orderDAO.isPresent(), "Order is present in the database");
        assertEquals(CUSTOMER_NAME, orderDAO.get().getCustomerName(), "Customer name is correct in the database");
        assertEquals(total, orderDAO.get().getTotal(), "Total is correct in the database");
    }

    @ParameterizedTest
    @MethodSource("invalidCustomerNameAndExpectedError")
    public void unsuccessfulOrderCreationWhenInvalidCustomerName(String invalidCustomerName, String expectedErrorMessage) throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerName(invalidCustomerName);
        orderRequest.setItems(List.of(orderItem(1, 1)));

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", Matchers.is(expectedErrorMessage)));
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void unsuccessfulOrderCreationWhenNullOrderItems(List<OrderItemRequest> orderItems) throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerName(CUSTOMER_NAME);
        orderRequest.setItems(orderItems);

        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orderRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", Matchers.is("Order items are required")));
    }

    @Test
    public void unsuccessfulOrderCreationWhenNullOrderRequest() throws Exception {
        mvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(null)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteOrderSuccessfully() throws Exception {
        OrderDAO orderDAO = insertNewOrderInDatabase();

        MvcResult productResult = mvc.perform(get("/products/id_1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Integer onHandBeforeDelete = JsonPath.read(productResult.getResponse().getContentAsString(), "$.onHand");

        mvc.perform(get(PATH + "/" + orderDAO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mvc.perform(delete(PATH + "/" + orderDAO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        mvc.perform(get(PATH + "/" + orderDAO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        mvc.perform(get("/products/id_1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.onHand", Matchers.is(onHandBeforeDelete + 1)));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Stream<Arguments> getOrderItemsRequest() {
        return Stream.of(
                Arguments.of(List.of(orderItem(1, 1)), BigDecimal.valueOf(10.00).setScale(2)),
                Arguments.of(List.of(orderItem(1, 1), orderItem(5, 2)), BigDecimal.valueOf(110.00).setScale(2)),
                Arguments.of(List.of(orderItem(2, 5), orderItem(3, 5)), BigDecimal.valueOf(250.00).setScale(2))
        );
    }

    private static Stream<Arguments> invalidCustomerNameAndExpectedError() {
        return Stream.of(
                Arguments.of(null, "Customer name is required"),
                Arguments.of("", "Customer name must be between 3 and 100 characters"),
                Arguments.of("a", "Customer name must be between 3 and 100 characters")
        );
    }

    private static OrderItemRequest orderItem(int number, int quantity) {
        return new OrderItemRequest(null, "id_" + number, quantity);
    }

    private void insertOrdersInDatabase(int numberOfOrders) {
        for (int i = 0; i < numberOfOrders; i++) {
            insertNewOrderInDatabase();
        }
    }

    private OrderDAO insertNewOrderInDatabase() {
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.setCustomerName(CUSTOMER_NAME);
        orderDAO.setActive(true);
        orderDAO.setLocalDateTime(LocalDateTime.now());
        orderDAO.setItems(List.of(new OrderItemDAO(null, BigInteger.ONE, BigDecimal.valueOf(10.00), true, new ProductDAO("id_1"), orderDAO)));
        orderDAO.setTotal(BigDecimal.valueOf(10.00));
        return ordersRepository.save(orderDAO);
    }
}

package com.challenge.api.controller;

import com.challenge.api.ApiApplication;
import com.challenge.api.repositories.OrderItemsRepository;
import com.challenge.api.repositories.OrdersRepository;
import com.challenge.api.repositories.ProductsRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ApiApplication.class)
@AutoConfigureMockMvc
public abstract class IntegrationTestsBase {
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ProductsRepository productsRepository;

    @Autowired
    protected OrderItemsRepository orderItemsRepository;

    @Autowired
    protected OrdersRepository ordersRepository;
}

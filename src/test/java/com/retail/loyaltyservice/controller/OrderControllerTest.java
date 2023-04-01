package com.retail.loyaltyservice.controller;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.Order;
import com.retail.loyaltyservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class OrderControllerTest {

    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    public void setUp() {
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);
    }

    @Test
    public void testCreate_Success() {
        Order orderToCreate = new Order(0L, BigDecimal.valueOf(45.3), 63, Customer.builder().build(), LocalDate.now());

        Order expectedResult = new Order(1L, BigDecimal.valueOf(45.3), 25, Customer.builder().build(), LocalDate.now());
        doReturn(expectedResult)
                .when(orderService)
                .create(any());

        ResponseEntity<Order> response = orderController.create(orderToCreate);

        verify(orderService).create(orderToCreate);
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void testGet_Success() {
        long orderId = 1L;
        Order expectedResult = new Order(orderId, BigDecimal.valueOf(45.3), 19, Customer.builder().build(),
                LocalDate.now());
        doReturn(expectedResult)
                .when(orderService)
                .get(orderId);

        ResponseEntity<Order> response = orderController.get(orderId);

        verify(orderService).get(orderId);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void testGetByCustomerId_Success() {
        long customerId = 1L;
        List<Order> expectedResultList = Arrays
                .asList(new Order(1L, BigDecimal.valueOf(45.3), 16, Customer.builder().build(), LocalDate.now()));

        doReturn(expectedResultList)
                .when(orderService)
                .getByCustomerId(customerId);

        ResponseEntity<List<Order>> response = orderController.getByCustomerId(customerId);

        verify(orderService).getByCustomerId(customerId);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(expectedResultList);
    }
}

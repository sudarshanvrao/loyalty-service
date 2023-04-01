package com.retail.loyaltyservice.controller;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

public class CustomerControllerTest {

    private CustomerService customerService;
    private CustomerController customerController;

    @BeforeEach
    public void setUp() {
        customerService = mock(CustomerService.class);
        customerController = new CustomerController(customerService);
    }

    @Test
    public void testCreate_Success() {
        Customer customerToCreate = new Customer(0L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala",50);
        long customerId = 1L;
        Customer expectedResult = new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 50);

        doReturn(expectedResult)
                .when(customerService)
                .create(any());

        ResponseEntity<Customer> response = customerController.create(customerToCreate);

        verify(customerService).create(customerToCreate);
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void testGet_Success() {
        long customerId = 1L;
        Customer expectedResult = new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 50);
        doReturn(expectedResult)
                .when(customerService)
                .get(customerId);

        ResponseEntity<Customer> response = customerController.get(customerId);

        verify(customerService).get(customerId);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody().getName()).isEqualTo(expectedResult.getName());
        assertThat(response.getBody().getLoyaltyPoints()).isEqualTo(expectedResult.getLoyaltyPoints());
    }

    @Test
    public void testUpdate_Success() {
        Customer customerToUpdate = new Customer(1L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala",50);
        long customerId = 1L;
        Optional<Customer> expectedResult = Optional.of(new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 50));

        doReturn(expectedResult)
                .when(customerService)
                .update(any());

        ResponseEntity<Customer> response = customerController.update(customerId, customerToUpdate);

        verify(customerService).update(customerToUpdate);
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody().getName()).isEqualTo(expectedResult.get().getName());
        assertThat(response.getBody().getLoyaltyPoints()).isEqualTo(expectedResult.get().getLoyaltyPoints());
    }

}

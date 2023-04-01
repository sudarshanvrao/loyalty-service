package com.retail.loyaltyservice.service;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class CustomerServiceTest {
    private CustomerRepository customerRepo;

    private CustomerService customerService;

    @BeforeEach
    public void setUp() {
        customerRepo = mock(CustomerRepository.class);
        customerService = new CustomerService(customerRepo);
    }

    @Test
    public void testCreate_Success() {
        Customer customer = new Customer(1L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 49);

        doReturn(customer)
                .when(customerRepo)
                .save(any());

        Customer savedCustomer = customerService.create(customer);

        verify(customerRepo).save(customer);
        assertThat(savedCustomer.getId() == customer.getId());
        assertThat(savedCustomer.getName() == customer.getName());
    }

    @Test
    public void testGet_Success() {
        Optional<Customer> expectedResult = Optional
                .of(new Customer(1L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala",49));

        doReturn(expectedResult)
                .when(customerRepo)
                .findById(anyLong());
        Customer customer = customerService.get(anyLong());

        verify(customerRepo).findById(anyLong());
        assertThat(customer.getId() == expectedResult.get().getId());
        assertThat(customer.getName() == expectedResult.get().getName());
    }

    @Test
    public void testGet_NoData_Exception() {
        long nonExistentCustomerId = 1L;

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            customerService.get(nonExistentCustomerId);
        });

        verify(customerRepo).findById(anyLong());

        String expectedMessage = "Customer not found for given Id: 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
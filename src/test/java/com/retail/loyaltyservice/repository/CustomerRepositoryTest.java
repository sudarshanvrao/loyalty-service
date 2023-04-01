package com.retail.loyaltyservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.retail.loyaltyservice.model.Customer;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepo;

    @Test
    public void testSave_And_FindById_Success() {
        Customer customerCreated = customerRepo
                .save(Customer.builder().name("John Doe").address("Cochin, Kerala").build());
        Optional<Customer> customer = customerRepo.findById(customerCreated.getId());

        assertTrue(customer.isPresent());
        assertNotNull(customer.get());
        assertEquals(customerCreated.getName(), customer.get().getName());
    }

    @Test
    public void testFindById_WithoutSave_Fail() {
        Long nonExistingCustomerId = 1L;
        Optional<Customer> customer = customerRepo.findById(nonExistingCustomerId);

        assertFalse(customer.isPresent());
    }

    @Test
    public void testFindById_Fail() {
        customerRepo.save(Customer.builder().name("John Doe").address("Cochin, Kerala").build());
        Long nonExistingCustomerId = 22L;
        Optional<Customer> customer = customerRepo.findById(nonExistingCustomerId);

        assertFalse(customer.isPresent());
    }
}

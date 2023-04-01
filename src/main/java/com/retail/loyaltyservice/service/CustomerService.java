package com.retail.loyaltyservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.repository.CustomerRepository;
import java.util.Optional;

/**
 * Customer API service implementation.
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepo;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepo = customerRepository;
    }

    /**
     * Method to create customer data.
     * 
     * @param customer
     * @return Customer
     */
    public Customer create(Customer customer) {
        return customerRepo.save(customer);
    }

    /**
     * Fetch customer from customerId.
     * 
     * @param customerId
     * @return Customer
     */
    public Customer get(Long customerId) {
        Optional<Customer> customer = customerRepo.findById(customerId);
        if (customer.isEmpty()) {
            throw new EntityNotFoundException("Customer not found for given Id: " + customerId);
        }
        return customer.get();
    }

    /**
     * Update customer.
     *
     * @param customer
     */
    public Optional<Customer> update(Customer customer) {
        customerRepo.update(customer.getId(), customer.getName(), customer.getEmail(), customer.getAddress(), customer.getLoyaltyPoints());
        return customerRepo.findById(customer.getId());
    }
}

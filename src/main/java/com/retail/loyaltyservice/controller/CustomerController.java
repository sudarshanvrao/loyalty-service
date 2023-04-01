package com.retail.loyaltyservice.controller;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller class to handle Customer operations.
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer customer) {
        return created(null)
                .body(customerService.create(customer));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Customer> get(@PathVariable(value = "id") Long customerId) {
        Customer customer = customerService.get(customerId);
        return ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        if (!id.equals(customer.getId())) {
            throw new IllegalArgumentException("Customer id mismatch in request");
        }
        Optional<Customer> updatedCustomer = customerService.update(customer);
        return created(null)
                .body(updatedCustomer.get());
    }
}

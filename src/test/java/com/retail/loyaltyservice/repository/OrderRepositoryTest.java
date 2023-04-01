package com.retail.loyaltyservice.repository;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Test
    public void testSave_And_FindById_Success() {
        Customer customerCreated = customerRepo
                .save(Customer.builder().name("John Doe").address("Cochin, Kerala").build());
        Order orderToCreate = new Order(0L, BigDecimal.valueOf(45.3), 49, customerCreated, LocalDate.now());
        Order savedOrder = orderRepo.save(orderToCreate);
        Optional<Order> order = orderRepo.findById(savedOrder.getId());

        assertTrue(order.isPresent());
        assertNotNull(order.get());
        assertEquals(savedOrder.getTotalAmount(), order.get().getTotalAmount());
    }

    @Test
    public void testFindById_WithoutSave_Fail() {
        Long nonExistingOrderId = 1L;
        Optional<Order> order = orderRepo.findById(nonExistingOrderId);

        assertFalse(order.isPresent());
    }

    @Test
    public void testFindByCustomer_Success() {
        Customer customerCreated = customerRepo
                .save(Customer.builder().name("John Doe").address("Cochin, Kerala").build());
        Order orderToCreate = new Order(0L, BigDecimal.valueOf(45.3), 49, customerCreated, LocalDate.now());
        orderRepo.save(orderToCreate);
        List<Order> order = orderRepo.findByCustomer(customerCreated);

        assertFalse(order.isEmpty());
        assertNotNull(order.get(0));
        assertNotNull(order.get(0).getCustomer());
        assertEquals(customerCreated.getId(), order.get(0).getCustomer().getId());
    }

    @Test
    public void testFindByCustomer_WithNoOrders_FetchFail() {
        Customer customerCreated1 = customerRepo
                .save(Customer.builder().name("John Doe").address("Cochin, Kerala").build());
        Customer customerCreated2 = customerRepo
                .save(Customer.builder().name("John Doe").address("Cochin, Kerala").build());

        orderRepo.save(Order.builder().customer(customerCreated1).build());
        List<Order> foundEntity = orderRepo.findByCustomer(customerCreated2);

        assertTrue(foundEntity.isEmpty());
    }
}

package com.retail.loyaltyservice.service;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.Order;
import com.retail.loyaltyservice.repository.CustomerRepository;
import com.retail.loyaltyservice.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class OrderServiceTest {

        private OrderRepository orderRepo;

        private CustomerRepository customerRepo;

        private OrderService orderService;

        @BeforeEach
        public void setUp() {
                orderRepo = mock(OrderRepository.class);
                customerRepo = mock(CustomerRepository.class);
                orderService = new OrderService(orderRepo, customerRepo);
        }

        @Test
        public void testCreate_Success() {
                Optional<Customer> customer = Optional
                                .of(new Customer(1L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 47));
                doReturn(customer)
                                .when(customerRepo)
                                .findById(anyLong());
                Order orderToCreate = new Order(0L, BigDecimal.valueOf(45.3), 47, customer.get(), LocalDate.now());

                Order expectedResult = new Order(1L, BigDecimal.valueOf(45.3), 47, customer.get(), LocalDate.now());
                doReturn(expectedResult)
                                .when(orderRepo)
                                .save(any());
                Order savedOrder = orderService.create(orderToCreate);

                verify(orderRepo).save(orderToCreate);
                assertThat(savedOrder.getId() == expectedResult.getId());
                assertTrue(savedOrder.getTotalAmount().compareTo(BigDecimal.valueOf(45.3)) == 0);
        }

        @Test
        public void testCreate_CustomerNotFound_Exception_Fail() {
                Order orderToCreate = new Order(0L, BigDecimal.valueOf(45.3), 47, Customer.builder().id(1L).build(), LocalDate.now());

                Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                        orderService.create(orderToCreate);
                });
                String expectedMessage = "Customer not found for given Id: 1";
                String actualMessage = exception.getMessage();

                assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        public void testGet_Success() {
                Customer customer = new Customer(1L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala",56);
                Optional<Order> expectedResult = Optional
                                .of(new Order(1L, BigDecimal.valueOf(45.3), 56, customer, LocalDate.now()));

                doReturn(expectedResult)
                                .when(orderRepo)
                                .findById(any());
                Order order = orderService.get(anyLong());

                verify(orderRepo).findById(anyLong());
                assertThat(order.getId() == expectedResult.get().getId());
                assertTrue(order.getTotalAmount().compareTo(BigDecimal.valueOf(45.3)) == 0);
        }

        @Test
        public void testGet_NoData() {
                long nonExistentOrderId = 1L;

                Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                        orderService.get(nonExistentOrderId);
                });

                verify(orderRepo).findById(anyLong());

                String expectedMessage = "Order not found for given Id: 1";
                String actualMessage = exception.getMessage();

                assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        public void testGetByCustomerId_Success() {
                Long customerId = 1L;
                Optional<Customer> customer = Optional
                                .of(new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala",45));

                doReturn(customer)
                                .when(customerRepo)
                                .findById(anyLong());
                List<Order> expectedResult = Arrays.asList(
                                new Order(1L, BigDecimal.valueOf(45.3), 45, customer.get(), LocalDate.now()));

                doReturn(expectedResult)
                                .when(orderRepo)
                                .findByCustomer(any());
                List<Order> orders = orderService.getByCustomerId(customerId);

                verify(orderRepo).findByCustomer(customer.get());
                assertThat(orders.get(0).getId() == expectedResult.get(0).getId());
                assertTrue(orders.get(0).getTotalAmount().compareTo(BigDecimal.valueOf(45.3)) == 0);
        }

        @Test
        public void testGetByCustomerId_NoData() {
                Long customerId = 1L;
                Optional<Customer> customer = Optional
                                .of(new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala",78));
                doReturn(customer)
                                .when(customerRepo)
                                .findById(anyLong());
                Exception exception = assertThrows(EntityNotFoundException.class, () -> {
                        orderService.getByCustomerId(customerId);
                });

                verify(customerRepo).findById(anyLong());

                String expectedMessage = "Orders not found for given Customer Id: 1";
                String actualMessage = exception.getMessage();

                assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        public void testCreate_NotEligibleForLoyaltyPoints() {
                Optional<Customer> customer = Optional
                                .of(new Customer(1L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 78));
                doReturn(customer)
                                .when(customerRepo)
                                .findById(anyLong());
                Order orderToCreate = new Order(0L, BigDecimal.valueOf(45.3), 0, customer.get(), LocalDate.now());
                Order expectedResult = new Order(1L, BigDecimal.valueOf(45.3), 0, customer.get(), LocalDate.now());
                doReturn(expectedResult)
                                .when(orderRepo)
                                .save(any());

                Order order = orderService.create(orderToCreate);

                verify(orderRepo).save(orderToCreate);
                assertThat(order.getLoyaltyPoints() == 0);
        }

        @Test
        public void testCreate_EligibleForLoyaltyPoints() {
                Optional<Customer> customer = Optional
                                .of(new Customer(1L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 82));
                doReturn(customer)
                                .when(customerRepo)
                                .findById(anyLong());
                Order orderToCreate = new Order(0L, BigDecimal.valueOf(120), 90, customer.get(), LocalDate.now());
                Order expectedResult = new Order(1L, BigDecimal.valueOf(120), 90, customer.get(), LocalDate.now());
                doReturn(expectedResult)
                                .when(orderRepo)
                                .save(any());

                Order order = orderService.create(orderToCreate);

                verify(orderRepo).save(orderToCreate);
                assertThat(order.getLoyaltyPoints() == 90);
        }
}
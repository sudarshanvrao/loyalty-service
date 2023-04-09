package com.retail.loyaltyservice.service;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.LoyaltySummary;
import com.retail.loyaltyservice.model.Order;
import com.retail.loyaltyservice.repository.CustomerRepository;
import com.retail.loyaltyservice.repository.OrderRepository;
import com.retail.loyaltyservice.util.LoyaltyPointsUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LoyaltyServiceTest {

    private CustomerRepository customerRepo;

    private OrderRepository orderRepo;

    private LoyaltyService loyaltyservice;

    @BeforeEach
    public void setUp() {
        customerRepo = mock(CustomerRepository.class);
        orderRepo = mock(OrderRepository.class);
        loyaltyservice = new LoyaltyService(customerRepo, orderRepo);
    }

    @Test
    public void testLoyaltyPoints_For_OrderAmounts() {
        int spendMultiplierAboveFifty = 1;
        int spendMultiplierAboveHundred = 2;
        BigDecimal orderAmountNotEligibleForLoyaltyPoints = BigDecimal.valueOf(50.58);
        assertEquals(0, LoyaltyPointsUtil.calculatePoints(orderAmountNotEligibleForLoyaltyPoints, spendMultiplierAboveFifty, spendMultiplierAboveHundred));

        BigDecimal orderAmountGreaterThan50 = BigDecimal.valueOf(75.50);
        assertEquals(25, LoyaltyPointsUtil.calculatePoints(orderAmountGreaterThan50, spendMultiplierAboveFifty, spendMultiplierAboveHundred));

        BigDecimal orderAmountGreaterThan100 = BigDecimal.valueOf(160.50);
        assertEquals(171, LoyaltyPointsUtil.calculatePoints(orderAmountGreaterThan100, spendMultiplierAboveFifty, spendMultiplierAboveHundred));
    }

    @Test
    public void testGetLoyaltyByCustomerId_Success() {
        Long customerId = 1L;
        Optional<Customer> expectedResult = Optional
                .of(new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 56));

        doReturn(expectedResult)
                .when(customerRepo)
                .findById(anyLong());
        int loyaltyPointsActual = loyaltyservice.getLoyaltyPoints(customerId);
        verify(customerRepo).findById(customerId);
        assertEquals(56, loyaltyPointsActual);
    }

    @Test
    public void testGetLoyaltySummary_Success() {
        Long customerId = 1L;
        LocalDate startDate = LocalDate.of(2023, 01, 01);
        LocalDate endDate = LocalDate.of(2023, 02, 01);
        Optional<Customer> expectedResult = Optional
                .of(new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 270));
        doReturn(expectedResult)
                .when(customerRepo)
                .findById(anyLong());
        Order order1 = new Order(1L, BigDecimal.valueOf(100), 50, expectedResult.get(), LocalDate.of(2023, 01,15));
        Order order2 = new Order(2L, BigDecimal.valueOf(120), 90, expectedResult.get(), LocalDate.of(2023, 01,15));
        List<Order> expectedOrders = Arrays.asList(order1,order2);
        doReturn(expectedOrders)
                .when(orderRepo)
                .findByCustomer(any());

        LoyaltySummary loyaltySummary = loyaltyservice.getLoyaltySummary(customerId, startDate, endDate);
        verify(orderRepo).findByCustomer(any());
        assertThat(loyaltySummary.getMonthlyLoyaltyPoints().size() > 0);
        assertEquals(loyaltySummary.getMonthlyLoyaltyPoints().get(0).getPoints(), 140);
    }

    @Test
    public void testGetLoyaltySummary_CustomerNotFound_Exception_Fail() {
        Long customerId = 1L;
        LocalDate startDate = LocalDate.of(2023, 01, 01);
        LocalDate endDate = LocalDate.of(2023, 02, 01);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            loyaltyservice.getLoyaltySummary(customerId, startDate, endDate);
        });
        String expectedMessage = "Customer not found for given Id: 1";
        String actualMessage = exception.getMessage();

        verify(customerRepo).findById(anyLong());
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetLoyaltySummary_NoLoyaltyPointsFound_Exception_Fail() {
        Long customerId = 1L;
        LocalDate startDate = LocalDate.of(2023, 01, 01);
        LocalDate endDate = LocalDate.of(2023, 02, 01);

        Optional<Customer> expectedResult = Optional
                .of(new Customer(customerId, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 0));
        doReturn(expectedResult)
                .when(customerRepo)
                .findById(anyLong());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            loyaltyservice.getLoyaltySummary(customerId, startDate, endDate);
        });
        String expectedMessage = "No Loyalty points awarded for customer yet";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
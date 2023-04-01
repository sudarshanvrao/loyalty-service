package com.retail.loyaltyservice.controller;

import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.LoyaltySummary;
import com.retail.loyaltyservice.model.MonthlyLoyaltyPoints;
import com.retail.loyaltyservice.service.LoyaltyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

public class LoyaltyControllerTest {

    private LoyaltyService loyaltyService;
    private LoyaltyController loyaltyController;

    @BeforeEach
    public void setUp() {
        loyaltyService = mock(LoyaltyService.class);
        loyaltyController = new LoyaltyController(loyaltyService);
    }

    @Test
    public void testGetLoyaltySummary_Success() {
        long customerId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);

        MonthlyLoyaltyPoints janLoyaltyPoints = MonthlyLoyaltyPoints.builder().month(01).year(2023).points(50).build();
        MonthlyLoyaltyPoints febLoyaltyPoints = MonthlyLoyaltyPoints.builder().month(02).year(2023).points(210).build();
        MonthlyLoyaltyPoints marchLoyaltyPoints = MonthlyLoyaltyPoints.builder().month(03).year(2023).points(10)
                .build();
        List<MonthlyLoyaltyPoints> monthlyLoyaltyPoints = Arrays.asList(janLoyaltyPoints, febLoyaltyPoints,
                marchLoyaltyPoints);

        LoyaltySummary expectedResult = LoyaltySummary.builder()
                .customer(Customer.builder().build())
                .monthlyLoyaltyPoints(monthlyLoyaltyPoints).totalPoints(270).build();
        doReturn(expectedResult)
                .when(loyaltyService)
                .getLoyaltySummary(customerId, startDate, endDate);

        ResponseEntity<LoyaltySummary> response = loyaltyController.getLoyaltySummary(customerId, startDate, endDate);

        verify(loyaltyService).getLoyaltySummary(customerId, startDate, endDate);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void testGetLoyaltyPoints_Success() {
        long customerId = 1L;
        int expectedResult = 50;
        doReturn(50)
                .when(loyaltyService)
                .getLoyaltyPoints(customerId);

        ResponseEntity<Integer> response = loyaltyController.getLoyaltyPoints(customerId);

        verify(loyaltyService).getLoyaltyPoints(customerId);
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }
}

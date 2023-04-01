package com.retail.loyaltyservice.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.LoyaltySummary;
import com.retail.loyaltyservice.model.MonthlyLoyaltyPoints;
import com.retail.loyaltyservice.service.CustomerService;
import com.retail.loyaltyservice.service.LoyaltyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoyaltyApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoyaltyService loyaltyService;

    @MockBean
    private CustomerService customerService;

    @Test
    public void testGetLoyaltyByCustomerId_Success_Status200() throws Exception {
        long customerId = 1L;
        int expectedResult = 50;
        doReturn(expectedResult)
                .when(loyaltyService)
                .getLoyaltyPoints(customerId);

        mockMvc.perform(get(getUrl() + "/1").contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string(asJsonString(expectedResult)));
    }

    @Test
    public void testGetLoyaltySummary_Success_Status200() throws Exception {
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
                .customer(Customer.builder().id(customerId).build())
                .monthlyLoyaltyPoints(monthlyLoyaltyPoints).totalPoints(270).build();
        doReturn(expectedResult)
                .when(loyaltyService)
                .getLoyaltySummary(customerId, startDate, endDate);

        mockMvc.perform(get(getUrl() + "/summary?customerId=1&startDate=2023-01-01&endDate=2023-03-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string(asJsonString(expectedResult)));
    }

    @Test
    public void testGetLoyaltySummary_Fail_Status404() throws Exception {
        long customerId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);

        doThrow(EntityNotFoundException.class)
                .when(loyaltyService).getLoyaltySummary(customerId, startDate, endDate);

        mockMvc.perform(get(getUrl() + "/summary?customerId=1&startDate=2023-01-01&endDate=2023-03-01")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Data does not exist")));
    }

    private static String getUrl() {
        return "/api/v1/loyalty";
    }

    /**
     * Converts object to json string.
     * 
     * @param obj
     * @return
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

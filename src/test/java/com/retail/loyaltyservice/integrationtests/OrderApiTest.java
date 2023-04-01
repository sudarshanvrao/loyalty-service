package com.retail.loyaltyservice.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.model.Order;
import com.retail.loyaltyservice.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreate_Success_Status201() throws Exception {
        Order order = new Order(0L, BigDecimal.valueOf(45.3), 49, Customer.builder().build(), null);
        doReturn(order)
                .when(orderService)
                .create(any());
        mockMvc.perform(post(getUrl()).content(asJsonString(order))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string(asJsonString(order)));
    }

    @Test
    public void testGet_Success_Status200() throws Exception {
        Order order = new Order(0L, BigDecimal.valueOf(45.3), 49, Customer.builder().build(), null);
        doReturn(order)
                .when(orderService)
                .get(anyLong());
        mockMvc.perform(get(getUrl() + "/1").contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string(asJsonString(order)));
    }

    @Test
    public void testGet_Fail_Status404() throws Exception {
        doThrow(EntityNotFoundException.class)
                .when(orderService).get(123L);
        mockMvc.perform(get(getUrl() + "/123").contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Data does not exist")));
    }

    @Test
    public void testGetByCustomerId_Success_Status200() throws Exception {
        List<Order> expectedResultList = Arrays
                .asList(new Order(1L, BigDecimal.valueOf(45.3), 49, Customer.builder().build(), null));
        doReturn(expectedResultList)
                .when(orderService)
                .getByCustomerId(anyLong());
        mockMvc.perform(get(getUrl() + "/customer/123").contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string(asJsonString(expectedResultList)));
    }

    @Test
    public void testGetByCustomerId_Fail_Status404() throws Exception {
        doThrow(EntityNotFoundException.class)
                .when(orderService).getByCustomerId(123L);
        mockMvc.perform(get(getUrl() + "/customer/123").contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Data does not exist")));
    }

    private static String getUrl() {
        return "/api/v1/orders";
    }

    /**
     * Converts object to json string.
     * 
     * @param obj
     * @return
     */
    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

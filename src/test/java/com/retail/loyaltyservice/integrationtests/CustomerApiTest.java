package com.retail.loyaltyservice.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.loyaltyservice.model.Customer;
import com.retail.loyaltyservice.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
public class CustomerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    public void testCreate_Success_Status201() throws Exception {
        Customer customer = new Customer(123L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 50);
        doReturn(customer)
                .when(customerService)
                .create(any());
        mockMvc.perform(post(getUrl()).content(asJsonString(customer))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string(asJsonString(customer)));
    }

    @Test
    public void testCreate_Fail_MalformedRequest_Status400() throws Exception {
        String customer = "";
        mockMvc.perform(post(getUrl()).content(asJsonString(customer))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Malformed JSON request")));
    }

    @Test
    public void testGet_Success_Status200() throws Exception {
        Customer customer = new Customer(123L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 50);
        doReturn(customer)
                .when(customerService)
                .get(anyLong());
        mockMvc.perform(get(getUrl() + "/1").contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().string(asJsonString(customer)));
    }

    @Test
    public void testGet_Fail_TypeMismatchStatus400() throws Exception {
        Customer customer = new Customer(123L, "John Doe", "johndoe@yahoo.com", "Cochin, Kerala", 50);
        doReturn(customer)
                .when(customerService)
                .get(anyLong());
        mockMvc.perform(get(getUrl() + "/abc").contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Type Mismatch")));
    }

    @Test
    public void testGet_Fail_Status404() throws Exception {
        doThrow(EntityNotFoundException.class)
                .when(customerService).get(123L);
        mockMvc.perform(get(getUrl() + "/123").contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Data does not exist")));
    }

    private static String getUrl() {
        return "/api/v1/customers";
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

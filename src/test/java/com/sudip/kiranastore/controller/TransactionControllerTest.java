package com.sudip.kiranastore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudip.kiranastore.entity.Transaction;
import com.sudip.kiranastore.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Test
    public void testRecordTransaction() throws Exception {
        // Mock data
        Transaction mockTransaction = new Transaction(/* provide necessary details */);
        Map<String, Object> mockResponse = Collections.singletonMap("status", "success");

        // Mocking service behavior
        Mockito.when(transactionService.recordTransaction(Mockito.anyString(), Mockito.any(Transaction.class)))
                .thenReturn(mockResponse);

        // Perform the request
        mockMvc.perform(post("/api/transactions")
                        .header("username", "testUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mockTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));

        // Verify that the service method was called
        Mockito.verify(transactionService, times(1)).recordTransaction(Mockito.anyString(), Mockito.any(Transaction.class));
    }

    @Test
    public void testGetAllTransactions() throws Exception {
        // Mock data
        Map<String, Object> mockResponse = Collections.singletonMap("status", "success");

        // Mocking service behavior
        Mockito.when(transactionService.getAllTransactions(Mockito.anyString())).thenReturn(mockResponse);

        // Perform the request
        mockMvc.perform(get("/api/transactions")
                        .header("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));

        // Verify that the service method was called
        Mockito.verify(transactionService, times(1)).getAllTransactions(Mockito.anyString());
    }

    @Test
    public void testGetDailyTransactionReport() throws Exception {
        // Mock data
        Map<String, Object> mockResponse = Collections.singletonMap("status", "success");

        // Mocking service behavior
        Mockito.when(transactionService.getDailyTransactionReport(Mockito.anyString(), Mockito.any(LocalDate.class)))
                .thenReturn(mockResponse);

        // Perform the request
        mockMvc.perform(get("/api/transactions/daily-report")
                        .header("username", "testUser")
                        .param("date", "2024-01-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));

        // Verify that the service method was called
        Mockito.verify(transactionService, times(1)).getDailyTransactionReport(Mockito.anyString(), Mockito.any(LocalDate.class));
    }

    // Helper method to convert objects to JSON strings
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

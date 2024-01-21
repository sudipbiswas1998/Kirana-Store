package com.sudip.kiranastore.service;

import com.sudip.kiranastore.constant.Currency;
import com.sudip.kiranastore.constant.ReasonCodes;
import com.sudip.kiranastore.constant.TransactionType;
import com.sudip.kiranastore.entity.Transaction;
import com.sudip.kiranastore.entity.User;
import com.sudip.kiranastore.repository.TransactionRepository;
import com.sudip.kiranastore.repository.UserRepository;
import com.sudip.kiranastore.service.impl.TransactionServiceImpl;
import com.sudip.kiranastore.util.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRecordTransaction_Successful() {
        // Arrange
        String username = "testUser";
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.TEN);
        transaction.setCurrency(Currency.USD);
        transaction.setType(TransactionType.CREDIT);

        User user = new User();
        user.setUsername(username);
        user.setDefaultCurrency(Currency.USD);
        user.setBalance(BigDecimal.ZERO);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(restTemplate.getForObject(any(), any())).thenReturn("{\"rates\":{\"USD\":1.0}}");

        // Act
        var response = transactionService.recordTransaction(username, transaction);

        // Assert
        verify(userRepository, times(1)).save(user);
        verify(transactionRepository, times(1)).save(transaction);
        assertEquals(ResponseUtils.successResponse("Transaction recorded successfully"), response);
    }

    @Test
    public void testRecordTransaction_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.TEN);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        var response = transactionService.recordTransaction(username, transaction);

        // Assert
        verify(userRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
        assertEquals(ResponseUtils.failureResponse("User not found", ReasonCodes.NOT_FOUND), response);
    }

    @Test
    public void testRecordTransaction_InvalidAmount() {
        // Arrange
        String username = "testUser";
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ZERO); // Invalid amount

        User user = new User();
        user.setUsername(username);
        user.setDefaultCurrency(Currency.USD);
        user.setBalance(BigDecimal.ZERO);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        var response = transactionService.recordTransaction(username, transaction);

        // Assert
        verify(userRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
        assertEquals(ResponseUtils.failureResponse("Invalid transaction amount. Amount should be positive.", ReasonCodes.INVALID_PARAMETER), response);
    }


    @Test
    public void testRecordTransaction_DebitTransactionAndUpdateBalance() {
        // Arrange
        String username = "testUser";
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.TEN);
        transaction.setType(TransactionType.DEBIT);

        User user = new User();
        user.setUsername(username);
        user.setDefaultCurrency(Currency.USD);
        user.setBalance(BigDecimal.valueOf(100));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(restTemplate.getForObject(any(), any())).thenReturn("{\"rates\":{\"USD\":1.0}}");

        // Act
        var response = transactionService.recordTransaction(username, transaction);

        // Assert
        verify(userRepository, times(1)).save(user);
        verify(transactionRepository, times(1)).save(transaction);
        assertEquals(ResponseUtils.successResponse("Transaction recorded successfully"), response);
        assertEquals(BigDecimal.valueOf(90), user.getBalance()); // Updated balance for debit transaction
    }

    @Test
    public void testGetAllTransactions_Successful() {
        // Arrange
        String username = "testUser";
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setType(TransactionType.CREDIT);
        transaction.setCurrency(Currency.USD);
        transaction.setTransactionDate(LocalDate.now());
        transaction.setDescription("Test transaction");

        when(transactionRepository.findAllByUser_Username(username)).thenReturn(List.of(transaction));

        // Act
        Map<String, Object> response = transactionService.getAllTransactions(username);

        // Assert
        assertEquals("success", response.get("status"));
    }



    @Test
    public void testRecordTransaction_CurrencyConversionUnsupported() {
        // Arrange
        String username = "testUser";
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.TEN);
        transaction.setCurrency(Currency.EUR); // Unsupported currency

        User user = new User();
        user.setUsername(username);
        user.setDefaultCurrency(Currency.USD);
        user.setBalance(BigDecimal.valueOf(100));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(restTemplate.getForObject(any(), any())).thenReturn("{\"rates\":{\"USD\":1.0}}");

        // Act and Assert
        var response = transactionService.recordTransaction(username, transaction);

        // Assert
        verify(userRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
        assertEquals("fail", response.get("status"));
    }



    @Test
    public void testGetDailyTransactionReport_Successful() {
        // Arrange
        String username = "testUser";
        LocalDate date = LocalDate.now();
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(BigDecimal.TEN);
        transaction.setType(TransactionType.CREDIT);
        transaction.setCurrency(Currency.USD);
        transaction.setTransactionDate(date);
        transaction.setDescription("Test transaction");

        when(transactionRepository.findAllByUser_UsernameAndTransactionDate(username, date)).thenReturn(List.of(transaction));

        // Act
        var response = transactionService.getDailyTransactionReport(username, date);

        // Assert
        assertEquals("success", response.get("status"));
    }

    @Test
    public void testGetDailyTransactionReport_NoTransactions() {
        // Arrange
        String username = "testUser";
        LocalDate date = LocalDate.now();

        when(transactionRepository.findAllByUser_UsernameAndTransactionDate(username, date)).thenReturn(List.of());

        // Act
        var response = transactionService.getDailyTransactionReport(username, date);

        // Assert
        assertEquals(ResponseUtils.successResponse(List.of()), response);
    }
}

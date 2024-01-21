package com.sudip.kiranastore.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudip.kiranastore.constant.Currency;
import com.sudip.kiranastore.constant.ReasonCodes;
import com.sudip.kiranastore.constant.TransactionType;
import com.sudip.kiranastore.entity.Transaction;
import com.sudip.kiranastore.entity.User;
import com.sudip.kiranastore.repository.TransactionRepository;
import com.sudip.kiranastore.repository.UserRepository;
import com.sudip.kiranastore.service.TransactionService;
import com.sudip.kiranastore.util.ResponseUtils;
import com.sudip.kiranastore.vo.TransactionResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${currency.rates.api}")
    private String apiUrl;

    /**
     * Records a transaction for the given user.
     *
     * @param username    The username of the user.
     * @param transaction The transaction to be recorded.
     * @return A map containing the result of the operation.
     */
    @Override
    public Map<String, Object> recordTransaction(String username, Transaction transaction) {
        log.info("Recording transaction for user: {}", username);

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            log.error("User not found: {}", username);
            return ResponseUtils.failureResponse("User not found", ReasonCodes.NOT_FOUND);
        }

        User user = userOptional.get();

        // Associate the transaction with the user
        transaction.setUser(user);

        // Validate the transaction
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid transaction amount. Amount should be positive.");
            return ResponseUtils.failureResponse("Invalid transaction amount. Amount should be positive.", ReasonCodes.INVALID_PARAMETER);
        }

        // Perform currency conversion
        if (transaction.getCurrency() != null && !transaction.getCurrency().equals(user.getDefaultCurrency())) {
            BigDecimal convertedAmount = BigDecimal.valueOf(0);
            BigDecimal exchangeRate = getExchangeRate(Currency.INR);

            if (exchangeRate == null) {
                log.error("Fetching exchange rate failed");
                return ResponseUtils.failureResponse("Exchange Rate fetching failed", ReasonCodes.NOT_FOUND);
            }

            log.info("Conversion rate is {}", exchangeRate);

            if (transaction.getCurrency().equals(Currency.INR)) {
                convertedAmount = transaction.getAmount().divide(exchangeRate, 2, RoundingMode.HALF_UP);
            } else if (transaction.getCurrency().equals(Currency.USD)) {
                convertedAmount = transaction.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
            }

            transaction.setAmount(convertedAmount);
            transaction.setCurrency(user.getDefaultCurrency());
        } else {
            transaction.setAmount(transaction.getAmount());
            transaction.setCurrency(transaction.getCurrency());
        }

        // Update user balance based on transaction type
        if (TransactionType.CREDIT.equals(transaction.getType())) {
            if (transaction.getAmount() != null) {
                user.setBalance(user.getBalance().add(transaction.getAmount()));
            }
        } else if (TransactionType.DEBIT.equals(transaction.getType()) && transaction.getAmount() != null) {
            user.setBalance(user.getBalance().subtract(transaction.getAmount()));
        }

        // Save the updated user balance
        userRepository.save(user);

        // Save the transaction
        transactionRepository.save(transaction);

        log.info("Transaction recorded successfully");
        return ResponseUtils.successResponse("Transaction recorded successfully");
    }


    /**
     * Retrieves all transactions for a given user from the database.
     *
     * @param username The username of the user.
     * @return A map containing the result of the operation and a list of transactions in JSON format.
     */
    public Map<String, Object> getAllTransactions(String username) {
        log.info("Fetching all transactions for user: {}", username);

        // Fetch all transactions for a given user from the database
        List<Transaction> transactions = transactionRepository.findAllByUser_Username(username);

        // Map transactions to a list of JSON objects
        List<TransactionResponseVo> transactionResponseVos = transactions.stream()
                .map(this::convertTransactionToMap)
                .collect(Collectors.toList());

        log.info("Fetched {} transactions for user: {}", transactions.size(), username);

        return ResponseUtils.successResponse(transactionResponseVos);
    }

    /**
     * Retrieves all transactions for a given user on a specific date from the database.
     *
     * @param username The username of the user.
     * @param date     The date for which transactions are to be retrieved.
     * @return A map containing the result of the operation and a list of transactions in JSON format.
     */
    @Override
    public Map<String, Object> getDailyTransactionReport(String username, LocalDate date) {
        log.info("Fetching daily transaction report for user: {} on date: {}", username, date);

        // Fetch all transactions for a given user and date from the database
        List<Transaction> transactions = transactionRepository.findAllByUser_UsernameAndTransactionDate(username, date);

        // Map transactions to a list of JSON objects
        List<TransactionResponseVo> transactionResponseVos = transactions.stream()
                .map(this::convertTransactionToMap)
                .collect(Collectors.toList());

        log.info("Fetched {} transactions for user: {} on date: {}", transactions.size(), username, date);

        return ResponseUtils.successResponse(transactionResponseVos);
    }


    /**
     * Retrieves the exchange rate between two currencies from the API.
     *
     * @param targetCurrency The target currency.
     * @return The exchange rate or null if fetching fails.
     */
    private BigDecimal getExchangeRate(Currency targetCurrency) {
        log.info("Fetching exchange rate for currency conversion");

        String apiResponse = restTemplate.getForObject(apiUrl, String.class);

        if (apiResponse == null) {
            log.error("API response is null");
            return null;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(apiResponse);

            JsonNode ratesNode = jsonNode.path("rates");
            JsonNode targetCurrencyNode = ratesNode.path(targetCurrency.name());

            return targetCurrencyNode.decimalValue();

        } catch (IOException e) {
            log.error("Error parsing API response", e);
            return null;
        }
    }


    /**
     * Converts a transaction to a map for response.
     *
     * @param transaction The transaction to be converted.
     * @return The map representing the transaction.
     */
    private TransactionResponseVo convertTransactionToMap(Transaction transaction) {
        TransactionResponseVo transactionResponseVo = new TransactionResponseVo();
        transactionResponseVo.setId(transaction.getId());
        transactionResponseVo.setAmount(transaction.getAmount());
        transactionResponseVo.setType(transaction.getType());
        transactionResponseVo.setCurrency(transaction.getCurrency());
        transactionResponseVo.setTransactionDate(transaction.getTransactionDate());
        transactionResponseVo.setDescription(transaction.getDescription());
        return transactionResponseVo;
    }
}

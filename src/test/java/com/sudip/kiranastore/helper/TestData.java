package com.sudip.kiranastore.helper;

import com.sudip.kiranastore.constant.Currency;
import com.sudip.kiranastore.constant.TransactionType;
import com.sudip.kiranastore.entity.Transaction;
import com.sudip.kiranastore.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TestData {
    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setDefaultCurrency(Currency.USD);
        user.setBalance(BigDecimal.valueOf(1000));
        return user;
    }

    public static Transaction createTestTransaction() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setCurrency(Currency.INR);
        transaction.setType(TransactionType.CREDIT);
        transaction.setTransactionDate(LocalDate.now());
        return transaction;
    }
    public static User createUser(String username, String password) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setBalance(BigDecimal.valueOf(0));
        user.setDefaultCurrency(Currency.USD);
        return user;
    }
}

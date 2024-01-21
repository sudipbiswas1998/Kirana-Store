package com.sudip.kiranastore.service;

import com.sudip.kiranastore.entity.Transaction;

import java.time.LocalDate;
import java.util.Map;


public interface TransactionService {
    Map<String, Object> recordTransaction(String userName, Transaction transaction);

    Map<String, Object> getAllTransactions(String userName);

    Map<String, Object> getDailyTransactionReport(String userName, LocalDate localDate);
}

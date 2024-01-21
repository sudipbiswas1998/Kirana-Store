package com.sudip.kiranastore.controller;

import com.sudip.kiranastore.entity.Transaction;
import com.sudip.kiranastore.service.TransactionService;
import com.sudip.kiranastore.util.BLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * Records a new transaction.
     *
     * @param userName     The username associated with the transaction.
     * @param transaction  The transaction details.
     * @return ResponseEntity containing the response map.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> recordTransaction(
            @RequestHeader("username") String userName, @RequestBody Transaction transaction) {
        Map<String, Object> response = transactionService.recordTransaction(userName, transaction);
        return sendResponse(response);
    }

    /**
     * Retrieves all transactions for a user.
     *
     * @param userName The username for which transactions are retrieved.
     * @return ResponseEntity containing the response map.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTransactions(@RequestHeader("username") String userName) {
        Map<String, Object> transactions = transactionService.getAllTransactions(userName);
        return sendResponse(transactions);
    }

    /**
     * Retrieves the daily transaction report for a user on a specific date.
     *
     * @param username The username for which the report is retrieved.
     * @param date     The specific date for the daily report.
     * @return ResponseEntity containing the response map.
     */
    @GetMapping("/daily-report")
    public ResponseEntity<Map<String, Object>> getDailyTransactionReport(
            @RequestHeader String username,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Map<String, Object> dailyReport = transactionService.getDailyTransactionReport(username, date);
        return sendResponse(dailyReport);
    }

    /**
     * Helper method to send the response based on the validation of the response status.
     *
     * @param responseMap The response map.
     * @return ResponseEntity containing the response map.
     */
    private ResponseEntity<Map<String, Object>> sendResponse(Map<String, Object> responseMap) {
        if (!BLUtil.validateResponseStatus(responseMap))
            return ResponseEntity.badRequest().body(responseMap);
        return ResponseEntity.ok(responseMap);
    }
}


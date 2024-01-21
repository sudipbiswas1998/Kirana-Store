package com.sudip.kiranastore.repository;

import com.sudip.kiranastore.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser_Username(String userName);

    List<Transaction> findAllByUser_UsernameAndTransactionDate(String username, LocalDate date);
}

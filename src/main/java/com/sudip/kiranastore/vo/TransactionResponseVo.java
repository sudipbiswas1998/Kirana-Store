package com.sudip.kiranastore.vo;

import com.sudip.kiranastore.constant.Currency;
import com.sudip.kiranastore.constant.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionResponseVo {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private Currency currency;
    private LocalDate transactionDate;
    private String description;
}

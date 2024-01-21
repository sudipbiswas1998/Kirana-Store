package com.sudip.kiranastore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sudip.kiranastore.constant.Currency;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "default_currency",nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency defaultCurrency;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Transaction> transactions;

}

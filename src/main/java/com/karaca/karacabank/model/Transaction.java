package com.karaca.karacabank.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.karaca.karacabank.constants.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private double transactionAmount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    @JsonBackReference
    private Account account;
}

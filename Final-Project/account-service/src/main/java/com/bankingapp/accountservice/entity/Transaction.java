package com.bankingapp.accountservice.entity;

import com.bankingapp.accountservice.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTIONS", indexes = {
        @Index(name = "IDX_TX_ACCOUNT", columnList = "ACCOUNT_ID")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @SequenceGenerator(name = "tx_seq", sequenceName = "TRANSACTIONS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tx_seq")
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Account account;

    @Column(name = "AMOUNT", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    public void prePersist() {
        if (transactionDate == null) transactionDate = LocalDateTime.now();
    }
}

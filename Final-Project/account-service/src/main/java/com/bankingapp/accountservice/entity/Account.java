package com.bankingapp.accountservice.entity;

import com.bankingapp.accountservice.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ACCOUNTS", indexes = {
        @Index(name = "IDX_ACCOUNT_NUMBER", columnList = "ACCOUNT_NUMBER")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @SequenceGenerator(name = "acct_seq", sequenceName = "ACCOUNTS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acct_seq")
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "ACCOUNT_NUMBER", nullable = false, unique = true, length = 30)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_TYPE", nullable = false, length = 30)
    private AccountType accountType;

    @Column(name = "BALANCE", precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "VERSION")
    private Long version;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (balance == null) balance = BigDecimal.ZERO;
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addTransaction(Transaction tx) {
        transactions.add(tx);
        tx.setAccount(this);
    }

    public void removeTransaction(Transaction tx) {
        transactions.remove(tx);
        tx.setAccount(null);
    }
}

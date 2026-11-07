package org.delcom.ifs25007pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Entity transaksi keuangan (pemasukan atau pengeluaran). */
public class Transaction {
    private final int id;
    private String description;
    private BigDecimal amount;
    private final TransactionType type;
    private final LocalDateTime createdAt;

    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, BigDecimal amount, TransactionType type,
                       LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    /** Nilai bertanda: positif untuk pemasukan, negatif untuk pengeluaran. */
    public BigDecimal signedAmount() {
        return isIncome() ? amount : amount.negate();
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

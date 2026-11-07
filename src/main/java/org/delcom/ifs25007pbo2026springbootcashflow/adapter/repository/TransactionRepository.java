package org.delcom.ifs25007pbo2026springbootcashflow.adapter.repository;

import org.delcom.ifs25007pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;

/** Repository transaksi in-memory dengan ID otomatis. */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer>
        implements ITransactionRepository {
    private int idCounter = 0;

    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction saveIncome(String description, BigDecimal amount) {
        return save(new Transaction(++idCounter, description, amount, TransactionType.INCOME));
    }

    @Override
    public Transaction saveExpense(String description, BigDecimal amount) {
        return save(new Transaction(++idCounter, description, amount, TransactionType.EXPENSE));
    }
}

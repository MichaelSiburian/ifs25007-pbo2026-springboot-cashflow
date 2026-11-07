package org.delcom.ifs25007pbo2026springbootcashflow.usecase;

import org.delcom.ifs25007pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.repository.ITransactionRepository;

import java.math.BigDecimal;
import java.util.List;

/** Logika bisnis catatan keuangan. Tanpa I/O UI. */
public class FinanceUseCase {
    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction addIncome(String description, String amountText) throws ValidationException {
        String desc = validateDescription(description);
        BigDecimal amount = parseAmount(amountText);
        return repository.saveIncome(desc, amount);
    }

    public Transaction addExpense(String description, String amountText) throws ValidationException {
        String desc = validateDescription(description);
        BigDecimal amount = parseAmount(amountText);
        return repository.saveExpense(desc, amount);
    }

    public void removeTransaction(int id) throws EntityNotFoundException {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Cari berdasarkan keterangan (case-insensitive); kata kunci kosong mengembalikan semua. */
    public List<Transaction> searchTransactions(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return repository.findAll();
        }
        String lower = keyword.trim().toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(lower));
    }

    public List<Transaction> sortTransactions(SortOption option) {
        return repository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    public BigDecimal getTotalIncome() {
        return repository.findAll().stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense() {
        return repository.findAll().stream()
                .filter(t -> !t.isIncome())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        return description.trim();
    }

    private BigDecimal parseAmount(String amountText) throws ValidationException {
        if (amountText == null || amountText.trim().isEmpty()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Jumlah harus berupa angka");
        }
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
        return amount;
    }
}

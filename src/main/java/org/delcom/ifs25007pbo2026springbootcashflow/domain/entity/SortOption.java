package org.delcom.ifs25007pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/** Kriteria pengurutan transaksi; setiap opsi membawa comparator-nya. */
public enum SortOption {
    AMOUNT_ASC("Jumlah terkecil", Comparator.comparing(Transaction::getAmount)),
    AMOUNT_DESC("Jumlah terbesar", Comparator.comparing(Transaction::getAmount).reversed()),
    INCOME_FIRST("Pemasukan dulu", Comparator.comparing((Transaction t) -> !t.isIncome())),
    EXPENSE_FIRST("Pengeluaran dulu", Comparator.comparing(Transaction::isIncome));

    private final String label;
    private final Comparator<Transaction> comparator;

    SortOption(String label, Comparator<Transaction> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String label() {
        return label;
    }

    public Comparator<Transaction> comparator() {
        return comparator;
    }
}

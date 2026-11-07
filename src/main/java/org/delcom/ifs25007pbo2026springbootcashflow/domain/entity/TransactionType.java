package org.delcom.ifs25007pbo2026springbootcashflow.domain.entity;

/** Jenis transaksi keuangan. */
public enum TransactionType {
    INCOME("Pemasukan"),
    EXPENSE("Pengeluaran");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

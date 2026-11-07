package org.delcom.ifs25007pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25007pbo2026springbootcashflow.domain.entity.Transaction;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Memformat data keuangan menjadi teks siap tampil. */
public class FinancePresenter {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** 1000000 -> "Rp 1,000,000.00". */
    public String formatMoney(BigDecimal amount) {
        String text = String.format(Locale.US, "%,.2f", amount.abs());
        return (amount.signum() < 0 ? "-Rp " : "Rp ") + text;
    }

    public String format(Transaction t) {
        return String.format("[%d] %s | %s | [%s]",
                t.getId(), t.getDescription(), formatMoney(t.getAmount()), t.getType().label());
    }

    public String formatCreatedAt(Transaction t) {
        return t.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String showTransactions(List<Transaction> list) {
        if (list.isEmpty()) {
            return "Daftar Transaksi:\n- Data transaksi belum tersedia!";
        }
        return "Daftar Transaksi:\n" + list.stream().map(this::format).collect(Collectors.joining("\n"));
    }

    public String showAddSuccess(Transaction t) {
        return "Berhasil menambah transaksi: " + format(t);
    }

    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showValidationError(String message) {
        return "[!] " + message;
    }
}

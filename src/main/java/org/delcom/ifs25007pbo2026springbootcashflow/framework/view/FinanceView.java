package org.delcom.ifs25007pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25007pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25007pbo2026springbootcashflow.usecase.FinanceUseCase;

import java.util.Comparator;
import java.util.List;

/** Tampilan web Vaadin untuk catatan keuangan (cashflow). */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {
    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<SortOption> sortBox = new ComboBox<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span("- Data transaksi belum tersedia!");
    private final Span totalCount = new Span("0");
    private final Span incomeValue = new Span();
    private final Span expenseValue = new Span();
    private final Span balanceValue = new Span();

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureToolbar();
        add(new H1("Cashflow"), buildSummary(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    private HorizontalLayout buildSummary() {
        HorizontalLayout row = new HorizontalLayout(
                card("Total Transaksi", totalCount, "#DBEAFE", "#1D4ED8"),
                card("Total Pemasukan", incomeValue, "#DCFCE7", "#16A34A"),
                card("Total Pengeluaran", expenseValue, "#FEE2E2", "#DC2626"),
                card("Saldo", balanceValue, "#FEF3C7", "#D97706")
        );
        row.setWidthFull();
        row.getStyle().set("align-items", "stretch");
        return row;
    }

    private Div card(String label, Span value, String background, String accent) {
        Span caption = new Span(label);
        caption.getStyle().set("display", "block").set("font-size", "0.875rem").set("color", accent);
        value.getStyle().set("display", "block").set("font-size", "1.5rem")
                .set("font-weight", "700").set("color", accent);
        Div card = new Div(caption, value);
        card.getStyle().set("flex", "1").set("min-width", "0").set("background", background)
                .set("border-left", "6px solid " + accent).set("border-radius", "12px")
                .set("padding", "1rem").set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        incomeValue.setText(presenter.formatMoney(useCase.getTotalIncome()));
        expenseValue.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceValue.setText(presenter.formatMoney(useCase.getBalance()));
    }

    private void configureGrid() {
        grid.addColumn(Transaction::getId).setHeader("ID").setKey("id")
                .setAutoWidth(true).setFlexGrow(0).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getId));
        grid.addColumn(Transaction::getDescription).setHeader("Keterangan").setKey("description")
                .setFlexGrow(1).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));
        grid.addColumn(t -> presenter.formatMoney(t.getAmount())).setHeader("Jumlah").setKey("amount")
                .setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getAmount));
        grid.addColumn(t -> t.getType().label()).setHeader("Tipe").setKey("type")
                .setAutoWidth(true).setSortable(true)
                .setComparator(SortOption.INCOME_FIRST.comparator());
        grid.addColumn(t -> presenter.formatCreatedAt(t)).setHeader("Waktu").setKey("createdAt")
                .setAutoWidth(true).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getCreatedAt));
        grid.addComponentColumn(this::createActions).setHeader("Tindakan").setKey("actions")
                .setAutoWidth(true).setFlexGrow(0);
        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private HorizontalLayout createActions(Transaction t) {
        Button delete = new Button("Hapus", VaadinIcon.TRASH.create(), e -> openDeleteDialog(t));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(delete);
    }

    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortBox.setPlaceholder("Urutkan...");
        sortBox.setItems(SortOption.values());
        sortBox.setItemLabelGenerator(SortOption::label);
        sortBox.setClearButtonVisible(true);
        sortBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                refreshGrid(useCase.sortTransactions(e.getValue()), "Daftar Transaksi (" + e.getValue().label() + ")");
            }
        });
    }

    private HorizontalLayout buildToolbar() {
        Button search = new Button("Cari", VaadinIcon.SEARCH.create(), e -> searchTransactions());
        Button reload = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), e -> refreshAll());
        Button income = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(), e -> openAddDialog(true));
        income.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        Button expense = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(), e -> openAddDialog(false));
        expense.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout bar = new HorizontalLayout(searchField, search, sortBox, reload, income, expense);
        bar.setWidthFull();
        bar.setFlexGrow(1, searchField);
        return bar;
    }

    private void openAddDialog(boolean isIncome) {
        TextField description = new TextField("Keterangan");
        description.setWidthFull();
        TextField amount = new TextField("Jumlah (Rp)");
        amount.setWidthFull();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(isIncome ? "Tambah Pemasukan" : "Tambah Pengeluaran");
        dialog.setWidth("450px");
        dialog.add(new VerticalLayout(description, amount));

        Button save = new Button("Simpan", VaadinIcon.CHECK.create(), e -> {
            try {
                Transaction created = isIncome
                        ? useCase.addIncome(description.getValue(), amount.getValue())
                        : useCase.addExpense(description.getValue(), amount.getValue());
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

    private void openDeleteDialog(Transaction t) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Menghapus Transaksi");
        dialog.add(new Paragraph("Yakin ingin menghapus transaksi \"" + t.getDescription()
                + "\" (ID " + t.getId() + ")?"));
        Button confirm = new Button("Hapus", VaadinIcon.TRASH.create(), e -> {
            deleteTransaction(t);
            dialog.close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        dialog.getFooter().add(cancel, confirm);
        dialog.open();
    }

    private void deleteTransaction(Transaction t) {
        try {
            useCase.removeTransaction(t.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
            refreshAll();
        }
    }

    private void searchTransactions() {
        String keyword = searchField.getValue();
        if (keyword.isBlank()) {
            refreshAll();
            return;
        }
        List<Transaction> results = useCase.searchTransactions(keyword);
        emptyState.setText("- Transaksi tidak ditemukan!");
        refreshGrid(results, "Hasil Pencarian: \"" + keyword + "\"");
    }

    private void refreshAll() {
        emptyState.setText("- Data transaksi belum tersedia!");
        searchField.clear();
        sortBox.clear();
        refreshGrid(useCase.getAllTransactions(), "Daftar Transaksi");
    }

    private void refreshGrid(List<Transaction> list, String title) {
        grid.setItems(list);
        updateSummary();
        statusLabel.setText(list.isEmpty()
                ? title + " - " + emptyState.getText().replaceFirst("^- ", "")
                : title);
    }

    private void showSuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}

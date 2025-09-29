/*
 * MIT License
 *
 * Copyright (c) 2025 Riccardo Catervi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * This software was designed and implemented as part of the academic
 * requirements of the "Programming Methodologies" course at
 * University of Camerino.
 */

package it.unicam.cs.mpgc.jbudget126139.ui.viewimpl;

import it.unicam.cs.mpgc.jbudget126139.controller.TagController;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.view.TransactionsView;
import it.unicam.cs.mpgc.jbudget126139.ui.util.FormatUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Implementation of the {@link TransactionsView} interface.
 * <p>
 * This view provides a table-based representation of all transactions,
 * including filtering capabilities (by type, category, and date range),
 * and action buttons for editing and deleting transactions.
 * </p>
 */
public class TransactionsViewImpl extends BaseViewImpl implements TransactionsView {

    private VBox root;
    private TagController tagController;
    private TableView<TransactionDTO> transactionsTable;
    private ObservableList<TransactionDTO> transactionsList = FXCollections.observableArrayList();
    private Currency currentCurrency = Currency.getInstance("USD");

    private ComboBox<String> typeFilter;
    private ComboBox<String> categoryFilter;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    private Runnable onAddTransaction;
    private Consumer<TransactionDTO> onEditTransaction;
    private Consumer<TransactionDTO> onDeleteTransaction;
    private Consumer<FilterData> onApplyFilters;
    private Runnable onClearFilters;

    /**
     * Creates a new instance of the transactions view.
     */
    public TransactionsViewImpl() {
        initialize();
    }

    private void initialize() {
        initializeBaseComponents();

        root = new VBox(30);
        root.getStyleClass().add("transactions-container");

        createHeader();
        createFilters();
        createTransactionsTable();

        StackPane containerWithOverlays = new StackPane();
        containerWithOverlays.getChildren().addAll(root, getLoadingOverlay(), getErrorContainer());
    }

    private void createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Transactions");
        title.getStyleClass().add("page-title");

        Button addButton = new Button("+ New Transaction");
        addButton.getStyleClass().add("add-transaction-btn");
        addButton.setOnAction(e -> {
            if (onAddTransaction != null) onAddTransaction.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, addButton);
        root.getChildren().add(header);
    }

    private void createFilters() {
        VBox filtersCard = new VBox(20);
        filtersCard.getStyleClass().add("filters-card");

        Label title = new Label("Filters");
        title.getStyleClass().add("card-subtitle");

        HBox filters = new HBox(20);
        filters.setAlignment(Pos.CENTER_LEFT);

        typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All Types", "Income", "Expense");
        typeFilter.setValue("All Types");
        typeFilter.getStyleClass().add("filter-combo");

        categoryFilter = new ComboBox<>();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.setValue("All Categories");
        categoryFilter.getStyleClass().add("filter-combo");

        startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        startDatePicker.getStyleClass().add("filter-date");

        endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        endDatePicker.getStyleClass().add("filter-date");

        Button applyBtn = new Button("Apply Filters");
        applyBtn.getStyleClass().add("filter-apply-btn");
        applyBtn.setOnAction(e -> applyFilters());

        filters.getChildren().addAll(
                new Label("Type:"), typeFilter,
                new Label("Category:"), categoryFilter,
                new Label("From:"), startDatePicker,
                new Label("To:"), endDatePicker,
                applyBtn
        );

        filtersCard.getChildren().addAll(title, filters);
        root.getChildren().add(filtersCard);
    }

    private void createTransactionsTable() {
        VBox tableCard = new VBox(20);
        tableCard.getStyleClass().add("transactions-table-card");

        transactionsTable = new TableView<>(transactionsList);
        transactionsTable.getStyleClass().add("modern-table");

        TableColumn<TransactionDTO, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().valueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        dateCol.setPrefWidth(120);

        TableColumn<TransactionDTO, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        TableColumn<TransactionDTO, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(
                hasValidTags(data.getValue()) ? "Tagged" : "Uncategorized"));
        categoryCol.setPrefWidth(120);

        TableColumn<TransactionDTO, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().direction() == TransactionDirection.CREDIT ? "Income" : "Expense"));
        typeCol.setPrefWidth(100);

        TableColumn<TransactionDTO, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> {
            TransactionDTO transaction = data.getValue();
            String prefix = transaction.direction() == TransactionDirection.CREDIT ? "+" : "-";
            String formattedAmount = FormatUtils.formatCurrency(transaction.amount(), currentCurrency);
            return new SimpleStringProperty(prefix + formattedAmount);
        });
        amountCol.setPrefWidth(120);

        TableColumn<TransactionDTO, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(120);
        actionsCol.setCellFactory(col -> new TableCell<TransactionDTO, Void>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("table-action-btn");
                deleteBtn.getStyleClass().add("table-delete-btn");
                buttons.setAlignment(Pos.CENTER);

                editBtn.setOnAction(e -> {
                    TransactionDTO transaction = getTableRow().getItem();
                    if (transaction != null && onEditTransaction != null) {
                        onEditTransaction.accept(transaction);
                    }
                });

                deleteBtn.setOnAction(e -> {
                    TransactionDTO transaction = getTableRow().getItem();
                    if (transaction != null && onDeleteTransaction != null) {
                        onDeleteTransaction.accept(transaction);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        transactionsTable.getColumns().addAll(dateCol, descCol, categoryCol, typeCol, amountCol, actionsCol);

        tableCard.getChildren().add(transactionsTable);
        root.getChildren().add(tableCard);
    }

    /**
     * Sets the currency used for formatting transaction amounts.
     *
     * @param currency the {@link Currency} to use
     */
    public void setCurrency(Currency currency) {
        this.currentCurrency = currency;
        if (transactionsTable != null) {
            transactionsTable.refresh();
        }
    }

    private void applyFilters() {
        if (onApplyFilters != null) {
            FilterData filterData = new FilterData(
                    typeFilter.getValue(),
                    categoryFilter.getValue(),
                    startDatePicker.getValue(),
                    endDatePicker.getValue()
            );
            onApplyFilters.accept(filterData);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Parent getRoot() {
        return root;
    }

    /** {@inheritDoc} */
    @Override
    public void displayTransactions(List<TransactionDTO> transactions) {
        transactionsList.clear();
        transactionsList.addAll(transactions);
    }

    /** {@inheritDoc} */
    @Override
    public void updateFilters(List<TagDTO> categories) {
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All Categories");
        for (TagDTO tag : categories) {
            categoryFilter.getItems().add(tag.name());
        }

        categoryFilter.setValue("All Categories");
    }


    @Override
    public void clearFilters() {
        typeFilter.setValue("All Types");
        categoryFilter.setValue("All Categories");
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        if (onClearFilters != null) {
            onClearFilters.run();
        }
    }

    /**
     * Sets the {@link TagController} to validate categories for transactions.
     *
     * @param tagController the tag controller
     */
    public void setTagController(TagController tagController) {
        this.tagController = tagController;
    }

    private boolean hasValidTags(TransactionDTO transaction) {
        if (transaction.tagIds().isEmpty()) return false;

        for (UUID tagId : transaction.tagIds()) {
            try {
                tagController.getTag(tagId);
                return true;
            } catch (Exception e) {

            }
        }
        return false;
    }

    public void setOnAddTransaction(Runnable handler) {
        this.onAddTransaction = handler;
    }

    public void setOnEditTransaction(Consumer<TransactionDTO> handler) {
        this.onEditTransaction = handler;
    }

    public void setOnDeleteTransaction(Consumer<TransactionDTO> handler) {
        this.onDeleteTransaction = handler;
    }

    public void setOnApplyFilters(Consumer<FilterData> handler) {
        this.onApplyFilters = handler;
    }

    public void setOnClearFilters(Runnable handler) {
        this.onClearFilters = handler;
    }

    /**
     * Data structure holding the filter criteria selected by the user.
     */
    public static class FilterData {
        public final String type;
        public final String category;
        public final java.time.LocalDate startDate;
        public final java.time.LocalDate endDate;

        /**
         * Creates a new filter data object.
         *
         * @param type      the transaction type filter ("Income", "Expense", or "All Types")
         * @param category  the category filter ("All Categories" or a tag name)
         * @param startDate the start date filter (nullable)
         * @param endDate   the end date filter (nullable)
         */
        public FilterData(String type, String category, java.time.LocalDate startDate, java.time.LocalDate endDate) {
            this.type = type;
            this.category = category;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
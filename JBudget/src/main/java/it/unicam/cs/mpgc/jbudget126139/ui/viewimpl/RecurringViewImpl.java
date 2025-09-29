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

import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.controller.RecurringControllerUI;
import it.unicam.cs.mpgc.jbudget126139.ui.view.RecurringView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Implementation of {@link RecurringView} for displaying and managing
 * recurring transactions within the UI.
 * <p>
 * Provides a table view to show recurring transactions with details
 * such as description, amount, type, frequency, and date range. Also
 * includes action buttons for editing, viewing, and deleting individual recurrences.
 * </p>
 */
public class RecurringViewImpl extends BaseViewImpl implements RecurringView {

    private RecurringControllerUI controller;
    private VBox root;
    private VBox contentContainer;
    private TableView<RecurrenceDTO> recurringTable;
    private ObservableList<RecurrenceDTO> recurrencesList = FXCollections.observableArrayList();

    private Runnable onAddRecurring;
    private Consumer<RecurrenceDTO> onEditRecurring;
    private Consumer<RecurrenceDTO> onDeleteRecurring;
    private Consumer<RecurrenceDTO> onViewRecurringTransactions;

    /**
     * Creates a new instance of the recurring transactions view.
     */
    public RecurringViewImpl() {
        initialize();
    }

    private void initialize() {
        initializeBaseComponents();

        root = new VBox(30);
        root.getStyleClass().add("recurring-container");

        createHeader();
        createContentContainer();

        StackPane containerWithOverlays = new StackPane();
        containerWithOverlays.getChildren().addAll(root, getLoadingOverlay(), getErrorContainer());
    }

    private void createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recurring Transactions");
        title.getStyleClass().add("page-title");

        Button addButton = new Button("+ Add Recurring");
        addButton.getStyleClass().add("add-recurring-btn");
        addButton.setOnAction(e -> {
            if (onAddRecurring != null) onAddRecurring.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, addButton);
        root.getChildren().add(header);
    }

    /**
     * Sets the controller responsible for handling recurrence-related logic.
     *
     * @param controller the {@link RecurringControllerUI} instance
     */
    public void setController(RecurringControllerUI controller) {
        this.controller = controller;
    }

    private void createContentContainer() {
        contentContainer = new VBox(20);
        createRecurringTable();
        root.getChildren().add(contentContainer);
    }

    private void createRecurringTable() {
        VBox tableCard = new VBox(20);
        tableCard.getStyleClass().add("transactions-table-card");

        recurringTable = new TableView<>(recurrencesList);
        recurringTable.getStyleClass().add("modern-table");

        TableColumn<RecurrenceDTO, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> {
            if (controller != null) {
                return new SimpleStringProperty(controller.getRecurrenceDescription(data.getValue()));
            }
            return new SimpleStringProperty("No controller");
        });
        descCol.prefWidthProperty().bind(recurringTable.widthProperty().multiply(0.25));

        TableColumn<RecurrenceDTO, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> {
            if (controller != null) {
                return new SimpleStringProperty(controller.getRecurrenceAmount(data.getValue()));
            }
            return new SimpleStringProperty("N/A");
        });
        amountCol.prefWidthProperty().bind(recurringTable.widthProperty().multiply(0.12));

        TableColumn<RecurrenceDTO, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            if (controller != null) {
                return new SimpleStringProperty(controller.getRecurrenceType(data.getValue()));
            }
            return new SimpleStringProperty("N/A");
        });
        typeCol.setPrefWidth(80);

        TableColumn<RecurrenceDTO, String> startDateCol = new TableColumn<>("Start Date");
        startDateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().startDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        startDateCol.setPrefWidth(120);

        TableColumn<RecurrenceDTO, String> endDateCol = new TableColumn<>("End Date");
        endDateCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().endDate() != null ?
                        data.getValue().endDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "No end date"));
        endDateCol.setPrefWidth(120);

        TableColumn<RecurrenceDTO, String> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().frequency().toString()));
        frequencyCol.setPrefWidth(100);

        TableColumn<RecurrenceDTO, String> transactionsCol = new TableColumn<>("Transactions");
        transactionsCol.setCellValueFactory(data -> {
            if (controller != null) {
                return new SimpleStringProperty(controller.getRecurrenceTransactionCount(data.getValue()));
            }
            return new SimpleStringProperty("0 transactions");
        });
        transactionsCol.setPrefWidth(120);

        TableColumn<RecurrenceDTO, Void> actionsCol = getRecurrenceDTOVoidTableColumn();

        recurringTable.getColumns().addAll(descCol, amountCol, typeCol, startDateCol, endDateCol,
                frequencyCol, transactionsCol, actionsCol);

        recurringTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        recurringTable.setPrefHeight(400);

        Label emptyLabel = new Label("No recurring transactions found");
        emptyLabel.getStyleClass().add("empty-table-label");
        emptyLabel.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
        recurringTable.setPlaceholder(emptyLabel);

        tableCard.getChildren().add(recurringTable);
        contentContainer.getChildren().add(tableCard);
    }

    private TableColumn<RecurrenceDTO, Void> getRecurrenceDTOVoidTableColumn() {
        TableColumn<RecurrenceDTO, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(190);
        actionsCol.setResizable(false);
        actionsCol.setCellFactory(col -> new TableCell<RecurrenceDTO, Void>() {
            private final Button editBtn = new Button("Edit");
            private final Button viewBtn = new Button("View");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(4, editBtn, viewBtn, deleteBtn);

            {
                editBtn.setPrefWidth(50);
                editBtn.setMinWidth(50);
                editBtn.setMaxWidth(50);
                editBtn.getStyleClass().add("table-action-btn");

                viewBtn.setPrefWidth(50);
                viewBtn.setMinWidth(50);
                viewBtn.setMaxWidth(50);
                viewBtn.getStyleClass().add("table-action-btn");

                deleteBtn.setPrefWidth(65);
                deleteBtn.setMinWidth(65);
                deleteBtn.setMaxWidth(65);
                deleteBtn.getStyleClass().add("table-delete-btn");

                buttons.setAlignment(Pos.CENTER);
                buttons.setPrefWidth(180);

                editBtn.setOnAction(e -> {
                    RecurrenceDTO recurrence = getTableRow().getItem();
                    if (recurrence != null && onEditRecurring != null) {
                        onEditRecurring.accept(recurrence);
                    }
                });

                viewBtn.setOnAction(e -> {
                    RecurrenceDTO recurrence = getTableRow().getItem();
                    if (recurrence != null && onViewRecurringTransactions != null) {
                        onViewRecurringTransactions.accept(recurrence);
                    }
                });

                deleteBtn.setOnAction(e -> {
                    RecurrenceDTO recurrence = getTableRow().getItem();
                    if (recurrence != null && onDeleteRecurring != null) {
                        onDeleteRecurring.accept(recurrence);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        return actionsCol;
    }

    /** {@inheritDoc} */
    @Override
    public Parent getRoot() {
        return root;
    }

    /** {@inheritDoc} */
    @Override
    public void displayRecurrences(List<RecurrenceDTO> recurrences) {
        recurrencesList.clear();

        if (recurrences.isEmpty()) {
            showEmptyState();
        } else {
            hideEmptyState();
            recurrencesList.addAll(recurrences);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void showEmptyState() {
        contentContainer.getChildren().clear();

        VBox emptyState = new VBox(20);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.getStyleClass().add("empty-state-container");

        Label emptyIcon = new Label("🔄");
        emptyIcon.getStyleClass().add("empty-state-icon");

        Label emptyText = new Label("No recurring transactions set up yet");
        emptyText.getStyleClass().add("empty-state-text");

        Label emptySubtext = new Label("Create recurring transactions for bills, subscriptions, and regular income");
        emptySubtext.getStyleClass().add("empty-state-subtext");

        emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubtext);
        contentContainer.getChildren().add(emptyState);
    }

    /** {@inheritDoc} */
    @Override
    public void hideEmptyState() {
        contentContainer.getChildren().clear();
        createRecurringTable();
    }

    /**
     * Sets the handler for adding a new recurring transaction.
     *
     * @param handler the action to execute
     */
    public void setOnAddRecurring(Runnable handler) {
        this.onAddRecurring = handler;
    }

    /**
     * Sets the handler for editing a recurring transaction.
     *
     * @param handler the action to execute with the recurrence
     */
    public void setOnEditRecurring(Consumer<RecurrenceDTO> handler) {
        this.onEditRecurring = handler;
    }

    /**
     * Sets the handler for deleting a recurring transaction.
     *
     * @param handler the action to execute with the recurrence
     */
    public void setOnDeleteRecurring(Consumer<RecurrenceDTO> handler) {
        this.onDeleteRecurring = handler;
    }

    /**
     * Sets the handler for viewing transactions linked to a recurrence.
     *
     * @param handler the action to execute with the recurrence
     */
    public void setOnViewRecurringTransactions(Consumer<RecurrenceDTO> handler) {
        this.onViewRecurringTransactions = handler;
    }
}
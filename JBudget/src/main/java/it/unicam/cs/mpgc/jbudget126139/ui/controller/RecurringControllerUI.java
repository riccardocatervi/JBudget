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

package it.unicam.cs.mpgc.jbudget126139.ui.controller;

import it.unicam.cs.mpgc.jbudget126139.controller.RecurrenceController;
import it.unicam.cs.mpgc.jbudget126139.controller.TagController;
import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.event.*;
import it.unicam.cs.mpgc.jbudget126139.ui.service.DialogService;
import it.unicam.cs.mpgc.jbudget126139.ui.view.RecurringView;
import it.unicam.cs.mpgc.jbudget126139.ui.viewimpl.RecurringViewImpl;
import it.unicam.cs.mpgc.jbudget126139.ui.util.FormatUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

/**
 * UI controller responsible for managing recurring transactions.
 * <p>
 * Orchestrates interactions between the recurrence, transaction, and tag controllers,
 * updates the {@link RecurringView}, and handles dialogs for CRUD operations.
 * </p>
 */
public class RecurringControllerUI implements UIController {

    private final RecurringView view;
    private final RecurrenceController recurrenceController;
    private final TransactionController transactionController;
    private final TagController tagController;
    private final RecurrenceService recurrenceService;
    private final DialogService dialogService;
    private final UIEventBus eventBus;

    private AccountDTO currentAccount;

    /**
     * Creates a new {@code RecurringControllerUI}.
     *
     * @param view                  the recurring view
     * @param recurrenceController  controller for recurrence operations
     * @param transactionController controller for transaction operations
     * @param tagController         controller for tag/category operations
     * @param recurrenceService     service for recurrence queries
     * @param dialogService         service for showing dialogs
     * @param eventBus              UI event bus
     */
    public RecurringControllerUI(RecurringView view,
                                 RecurrenceController recurrenceController,
                                 TransactionController transactionController,
                                 TagController tagController,
                                 RecurrenceService recurrenceService,
                                 DialogService dialogService,
                                 UIEventBus eventBus) {
        this.view = view;
        this.recurrenceController = recurrenceController;
        this.transactionController = transactionController;
        this.tagController = tagController;
        this.recurrenceService = recurrenceService;
        this.dialogService = dialogService;
        this.eventBus = eventBus;
    }

    /**
     * Initializes the controller: subscribes to account selection events,
     * wires view callbacks, and loads data if an account is already selected.
     */
    @Override
    public void initialize() {
        eventBus.subscribe(AccountSelectedEvent.class, this::onAccountSelected);

        if (view instanceof RecurringViewImpl impl) {
            impl.setOnAddRecurring(this::showAddRecurringDialog);
            impl.setOnEditRecurring(this::showEditRecurringDialog);
            impl.setOnDeleteRecurring(this::showDeleteRecurringDialog);
            impl.setOnViewRecurringTransactions(this::showRecurringTransactions);
        }

        if (currentAccount != null) loadRecurrences();
    }

    /**
     * Unsubscribes from UI events and performs cleanup.
     */
    @Override
    public void cleanup() {
        eventBus.unsubscribe(AccountSelectedEvent.class, this::onAccountSelected);
    }

    /**
     * Returns the associated view.
     *
     * @return the {@link RecurringView}
     */
    public RecurringView getView() {
        return view;
    }

    /**
     * Sets the current account and refreshes the recurrences list.
     *
     * @param account the selected account
     */
    public void setCurrentAccount(AccountDTO account) {
        this.currentAccount = account;
        loadRecurrences();
    }

    /**
     * Opens the dialog to add a new recurring transaction.
     * Loads the tag hierarchy to be used as categories in the dialog.
     */
    public void showAddRecurringDialog() {
        if (currentAccount == null) {
            dialogService.showErrorAlert("Please select an account first.");
            return;
        }
        try {
            List<TagDTO> rootTags = tagController.listRootTags();
            List<TagDTO> allTags = new ArrayList<>(rootTags);
            for (TagDTO rootTag : rootTags) allTags.addAll(loadChildTagsRecursively(rootTag));

            dialogService.showAddRecurringDialog(
                    currentAccount,
                    allTags,
                    r -> {
                        loadRecurrences();
                        eventBus.publish(new DataRefreshEvent(DataRefreshEvent.DataType.TRANSACTIONS, null));
                        dialogService.showSuccessAlert("Recurring transaction created successfully!");
                    },
                    error -> view.showError(error)
            );
        } catch (Exception e) {
            view.showError("Failed to open add recurring dialog: " + e.getMessage());
        }
    }

    /**
     * Opens the dialog to edit an existing recurring transaction.
     *
     * @param recurrence the recurrence to edit
     */
    public void showEditRecurringDialog(RecurrenceDTO recurrence) {
        dialogService.showEditRecurrenceDialog(
                recurrence,
                r -> {
                    loadRecurrences();
                    dialogService.showSuccessAlert("Recurring transaction updated successfully!");
                },
                error -> view.showError(error)
        );
    }

    /**
     * Confirms and deletes a recurrence along with its associated transactions.
     *
     * @param recurrence the recurrence to delete
     */
    public void showDeleteRecurringDialog(RecurrenceDTO recurrence) {
        boolean confirmed = dialogService.showConfirmationDialog(
                "Delete Recurring Transaction",
                "Delete Recurring Transaction?",
                "Are you sure you want to delete this recurring transaction? This will also delete all associated transactions."
        );
        if (!confirmed) return;
        try {
            List<TransactionDTO> associatedTransactions = transactionController.listByRecurrence(recurrence.id());

            for (TransactionDTO tx : associatedTransactions) {
                try {
                    transactionController.deleteTransaction(currentAccount.id(), tx.id());
                    eventBus.publish(new TransactionEvent(tx, TransactionEvent.TransactionAction.DELETED));
                    eventBus.publish(new TransactionChangedEvent(
                            tx,
                            TransactionChangedEvent.TransactionAction.DELETED,
                            currentAccount.id()
                    ));
                } catch (Exception e) {
                    System.err.println("Failed to delete transaction " + tx.id() + ": " + e.getMessage());
                }
            }
            recurrenceController.deleteRecurrence(recurrence.id());
            loadRecurrences();

            dialogService.showSuccessAlert("Recurring transaction and " + associatedTransactions.size()
                    + " associated transactions deleted successfully!");
        } catch (Exception ex) {
            view.showError("Error deleting recurring transaction: " + ex.getMessage());
        }
    }

    /**
     * Shows a modal dialog listing all transactions generated by the given recurrence.
     *
     * @param recurrence the recurrence whose transactions will be displayed
     */
    public void showRecurringTransactions(RecurrenceDTO recurrence) {
        try {
            List<TransactionDTO> recurrenceTransactions = transactionController.listByRecurrence(recurrence.id());

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Recurring Transaction Details");
            dialog.setWidth(800);
            dialog.setHeight(600);

            VBox content = new VBox(20);
            content.setPadding(new Insets(20));

            Label title = new Label("Transactions for " + recurrence.frequency() + " Recurrence");
            title.getStyleClass().add("dialog-title");

            TableView<TransactionDTO> table = new TableView<>();
            table.getItems().addAll(recurrenceTransactions);

            TableColumn<TransactionDTO, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().valueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));

            TableColumn<TransactionDTO, String> descCol = new TableColumn<>("Description");
            descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

            TableColumn<TransactionDTO, String> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(data -> new SimpleStringProperty(
                    FormatUtils.formatCurrency(data.getValue().amount(), getCurrentCurrency())));

            TableColumn<TransactionDTO, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().direction() == TransactionDirection.CREDIT ? "Income" : "Expense"));

            table.getColumns().addAll(dateCol, descCol, amountCol, typeCol);

            javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("Close");
            closeBtn.setOnAction(e -> dialog.close());

            content.getChildren().addAll(title, table, closeBtn);

            Scene scene = new Scene(content);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        } catch (Exception e) {
            view.showError("Error loading recurrence transactions: " + e.getMessage());
        }
    }

    /**
     * Returns a short description to display for a recurrence, derived from its first transaction if available.
     *
     * @param recurrence the recurrence
     * @return a short description, a fallback message, or an error message
     */
    public String getRecurrenceDescription(RecurrenceDTO recurrence) {
        try {
            List<TransactionDTO> transactions = transactionController.listByRecurrence(recurrence.id());
            if (!transactions.isEmpty()) {
                String description = transactions.get(0).description();
                if (description != null && !description.trim().isEmpty())
                    return description.length() > 40 ? description.substring(0, 37) + "..." : description;
            }
            return "No description";
        } catch (Exception e) {
            return "Error loading";
        }
    }

    /**
     * Returns the formatted amount preview for the recurrence (based on its first transaction).
     *
     * @param recurrence the recurrence
     * @return formatted amount with sign, or {@code "N/A"} on missing data, or {@code "Error"} on failure
     */
    public String getRecurrenceAmount(RecurrenceDTO recurrence) {
        try {
            List<TransactionDTO> transactions = transactionController.listByRecurrence(recurrence.id());
            if (!transactions.isEmpty()) {
                TransactionDTO first = transactions.get(0);
                String prefix = first.direction() == TransactionDirection.CREDIT ? "+" : "-";
                return prefix + FormatUtils.formatCurrency(first.amount(), getCurrentCurrency());
            }
            return "N/A";
        } catch (Exception e) {
            return "Error";
        }
    }

    /**
     * Returns the human-readable type of the recurrence (Income/Expense), using its first transaction if available.
     *
     * @param recurrence the recurrence
     * @return the type string, {@code "N/A"} if unknown, or {@code "Error"} on failure
     */
    public String getRecurrenceType(RecurrenceDTO recurrence) {
        try {
            List<TransactionDTO> transactions = transactionController.listByRecurrence(recurrence.id());
            if (!transactions.isEmpty()) {
                TransactionDTO first = transactions.get(0);
                return first.direction() == TransactionDirection.CREDIT ? "Income" : "Expense";
            }
            return "N/A";
        } catch (Exception e) {
            return "Error";
        }
    }

    /**
     * Returns a human-readable count of transactions generated by this recurrence.
     *
     * @param recurrence the recurrence
     * @return a string like {@code "3 transactions"} or {@code "0 transactions"} on failure
     */
    public String getRecurrenceTransactionCount(RecurrenceDTO recurrence) {
        try {
            List<TransactionDTO> transactions = transactionController.listByRecurrence(recurrence.id());
            int count = transactions.size();
            return count + " transaction" + (count != 1 ? "s" : "");
        } catch (Exception e) {
            return "0 transactions";
        }
    }

    /**
     * Returns the currency of the currently selected account, falling back to USD if none is selected.
     *
     * @return the current {@link Currency}
     */
    private Currency getCurrentCurrency() {
        return currentAccount != null ? currentAccount.currency() : Currency.getInstance("USD");
    }

    /**
     * Handles account selection events by updating the current account and reloading data.
     *
     * @param event the account selection event
     */
    private void onAccountSelected(AccountSelectedEvent event) {
        setCurrentAccount(event.account());
    }

    /**
     * Loads the recurrences for the current account and displays them in the view.
     */
    private void loadRecurrences() {
        if (currentAccount == null) {
            view.showEmptyState();
            return;
        }
        try {
            view.showLoading();
            List<RecurrenceDTO> recurrences = recurrenceService.listRecurrencesByAccount(currentAccount.id());
            view.displayRecurrences(recurrences);
            view.hideLoading();
        } catch (Exception e) {
            view.showError("Failed to load recurring transactions: " + e.getMessage());
            view.hideLoading();
        }
    }

    /**
     * Recursively loads child tags for the given parent tag.
     *
     * @param parent the parent tag
     * @return the flattened list of descendant tags including direct children
     */
    private List<TagDTO> loadChildTagsRecursively(TagDTO parent) {
        List<TagDTO> result = new ArrayList<>();
        try {
            List<TagDTO> children = tagController.listChildTags(parent.id());
            for (TagDTO child : children) {
                result.add(child);
                result.addAll(loadChildTagsRecursively(child));
            }
        } catch (Exception ignored) {
        }
        return result;
    }
}
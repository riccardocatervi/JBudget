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

package it.unicam.cs.mpgc.jbudget126139.ui.service;

import it.unicam.cs.mpgc.jbudget126139.controller.AccountController;
import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.controller.TagController;
import it.unicam.cs.mpgc.jbudget126139.controller.RecurrenceController;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.RecurrenceDTO;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceService;
import it.unicam.cs.mpgc.jbudget126139.ui.dialog.*;

import it.unicam.cs.mpgc.jbudget126139.ui.event.UIEventBus;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Centralized service for managing all application dialogs.
 * <p>
 * Encapsulates the creation and handling of dialogs related to
 * accounts, transactions, categories (tags), and recurring transactions.
 * Provides a consistent way to open dialogs, capture results, and
 * propagate success/error callbacks to the caller.
 * </p>
 * <p>
 * This service improves code reuse and keeps UI controllers free
 * from dialog instantiation details, promoting separation of concerns.
 * </p>
 */
public class DialogService {

    private final AccountController accountController;
    private final TransactionController transactionController;
    private final TagController tagController;
    private final RecurrenceController recurrenceController;
    private final RecurrenceService recurrenceService;
    private final UIEventBus eventBus;

    /**
     * Constructs a new {@code DialogService} with all required dependencies.
     *
     * @param accountController    controller for account management
     * @param transactionController controller for transaction management
     * @param tagController         controller for category/tag management
     * @param recurrenceController  controller for recurring transaction management
     * @param recurrenceService     service for recurrence operations
     * @param eventBus              UI event bus for publishing events
     */
    public DialogService(AccountController accountController,
                         TransactionController transactionController,
                         TagController tagController,
                         RecurrenceController recurrenceController,
                         RecurrenceService recurrenceService,
                         UIEventBus eventBus) {
        this.accountController = accountController;
        this.transactionController = transactionController;
        this.tagController = tagController;
        this.recurrenceController = recurrenceController;
        this.recurrenceService = recurrenceService;
        this.eventBus = eventBus;
    }

    /**
     * Shows a dialog to create a new account.
     *
     * @param onSuccess callback invoked when account creation succeeds
     * @param onError   callback invoked when an error occurs
     */
    public void showAddAccountDialog(Consumer<AccountDTO> onSuccess, Consumer<String> onError) {
        try {
            AddAccountDialog dialog = new AddAccountDialog(accountController);
            Optional<AccountDTO> result = dialog.showDialogAndWait();
            result.ifPresent(onSuccess);
        } catch (Exception e) {
            onError.accept("Failed to create account: " + e.getMessage());
        }
    }

    /**
     * Shows a dialog to edit an existing account.
     *
     * @param account   the account to edit
     * @param onSuccess callback invoked when account update succeeds
     * @param onError   callback invoked when an error occurs
     */
    public void showEditAccountDialog(AccountDTO account, Consumer<AccountDTO> onSuccess, Consumer<String> onError) {
        try {
            EditAccountDialog dialog = new EditAccountDialog(account, accountController);
            Optional<AccountDTO> result = dialog.showDialogAndWait();
            result.ifPresent(onSuccess);
        } catch (Exception e) {
            onError.accept("Failed to update account: " + e.getMessage());
        }
    }

    /**
     * Shows a confirmation dialog to delete an account, including
     * options to reassign or remove related transactions.
     *
     * @param account     the account to delete
     * @param allAccounts list of all available accounts (for reassignment)
     * @param onSuccess   callback invoked if deletion is confirmed
     * @param onError     callback invoked if an error occurs
     */
    public void showDeleteAccountDialog(AccountDTO account, List<AccountDTO> allAccounts,
                                        Runnable onSuccess, Consumer<String> onError) {
        try {
            DeleteAccountDialog dialog = new DeleteAccountDialog(
                    account,
                    allAccounts,
                    transactionController,
                    recurrenceController,
                    accountController,
                    null
            );

            boolean confirmed = dialog.showAndWait();
            if (confirmed)
                onSuccess.run();

        } catch (Exception e) {
            onError.accept("Failed to delete account: " + e.getMessage());
        }
    }

    /**
     * Shows a dialog to create a new transaction for the given account.
     *
     * @param currentAccount the account in which the transaction will be created
     * @param availableTags  list of tags/categories to assign
     * @param onSuccess      callback invoked when creation succeeds
     * @param onError        callback invoked when an error occurs
     */
    public void showAddTransactionDialog(AccountDTO currentAccount, List<TagDTO> availableTags,
                                         Consumer<TransactionDTO> onSuccess, Consumer<String> onError) {
        try {
            AddTransactionDialog dialog = new AddTransactionDialog(currentAccount, availableTags, transactionController);
            Optional<TransactionDTO> result = dialog.showDialogAndWait();
            result.ifPresent(onSuccess);
        } catch (Exception e) {
            onError.accept("Failed to create transaction: " + e.getMessage());
        }
    }

    /**
     * Shows a dialog to edit an existing transaction.
     *
     * @param transaction   the transaction to edit
     * @param currentAccount the account owning the transaction
     * @param availableTags list of tags/categories to assign
     * @param onSuccess     callback invoked when update succeeds
     * @param onError       callback invoked when an error occurs
     */
    public void showEditTransactionDialog(TransactionDTO transaction, AccountDTO currentAccount,
                                          List<TagDTO> availableTags, Consumer<TransactionDTO> onSuccess,
                                          Consumer<String> onError) {
        try {
            EditTransactionDialog dialog = new EditTransactionDialog(transaction, currentAccount,
                    availableTags, transactionController);
            Optional<TransactionDTO> result = dialog.showDialogAndWait();
            result.ifPresent(onSuccess);
        } catch (Exception e) {
            onError.accept("Failed to update transaction: " + e.getMessage());
        }
    }

    /**
     * Shows a confirmation dialog to delete a transaction.
     *
     * @param transaction   the transaction to delete
     * @param currentAccount the account owning the transaction
     * @param onSuccess     callback invoked if deletion succeeds
     * @param onError       callback invoked if an error occurs
     */
    public void showDeleteTransactionDialog(TransactionDTO transaction, AccountDTO currentAccount,
                                            Runnable onSuccess, Consumer<String> onError) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Transaction");
        alert.setHeaderText("Delete Transaction?");
        alert.setContentText("Are you sure you want to delete this transaction? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                transactionController.deleteTransaction(currentAccount.id(), transaction.id());
                onSuccess.run();
            } catch (Exception e) {
                onError.accept("Failed to delete transaction: " + e.getMessage());
            }
        }
    }

    /**
     * Shows a dialog to create a new tag (or subcategory).
     *
     * @param parent   optional parent tag (null for root category)
     * @param onSuccess callback invoked when creation succeeds
     * @param onError   callback invoked when an error occurs
     */
    public void showAddTagDialog(TagDTO parent, Consumer<TagDTO> onSuccess, Consumer<String> onError) {
        try {
            AddTagDialog dialog = new AddTagDialog(parent, tagController);
            Optional<TagDTO> result = dialog.showDialogAndWait();
            result.ifPresent(onSuccess);
        } catch (Exception e) {
            onError.accept("Failed to create category: " + e.getMessage());
        }
    }

    /**
     * Shows a dialog to edit an existing tag.
     *
     * @param tag      the tag to edit
     * @param onSuccess callback invoked when update succeeds
     * @param onError   callback invoked when an error occurs
     */
    public void showEditTagDialog(TagDTO tag, Consumer<TagDTO> onSuccess, Consumer<String> onError) {
        try {
            EditTagDialog dialog = new EditTagDialog(tag, tagController);
            Optional<TagDTO> result = dialog.showDialogAndWait();
            result.ifPresent(onSuccess);
        } catch (Exception e) {
            onError.accept("Failed to update category: " + e.getMessage());
        }
    }

    /**
     * Shows a dialog to create a new recurring transaction.
     *
     * @param currentAccount the account where recurrence applies
     * @param availableTags  list of categories to assign
     * @param onSuccess      callback invoked when creation succeeds
     * @param onError        callback invoked when an error occurs
     */
    public void showAddRecurringDialog(AccountDTO currentAccount, List<TagDTO> availableTags,
                                       Consumer<RecurrenceDTO> onSuccess, Consumer<String> onError) {
        try {
            AddRecurringTransactionDialog dialog = new AddRecurringTransactionDialog(
                    currentAccount, availableTags, recurrenceService, transactionController, eventBus);
            Optional<RecurrenceDTO> result = dialog.showDialogAndWait();
            result.ifPresent(onSuccess);
        } catch (Exception e) {
            onError.accept("Failed to create recurring transaction: " + e.getMessage());
        }
    }

    /**
     * Shows a dialog to edit an existing recurring transaction.
     *
     * @param recurrence the recurrence to edit
     * @param onSuccess  callback invoked when update succeeds
     * @param onError    callback invoked when an error occurs
     */
    public void showEditRecurrenceDialog(RecurrenceDTO recurrence, Consumer<RecurrenceDTO> onSuccess,
                                         Consumer<String> onError) {
        try {
            EditRecurringTransactionDialog dialog = new EditRecurringTransactionDialog(recurrence,
                    transactionController, recurrenceController, recurrenceService);
            Optional<RecurrenceDTO> result = dialog.showDialogAndWait();
            if (result.isPresent() && result.get() != null) {
                onSuccess.accept(result.get());
            }
        } catch (Exception e) {
            onError.accept("Failed to update recurring transaction: " + e.getMessage());
        }
    }

    /**
     * Shows a simple information alert with a success message.
     *
     * @param message the message to display (default message used if null)
     */
    public void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message != null ? message : "Operation completed successfully!");
        alert.showAndWait();
    }

    /**
     * Shows an error alert with the given message.
     *
     * @param message the error message to display
     */
    public void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirmation dialog with customizable title, header, and content.
     *
     * @param title   the dialog title
     * @param header  the dialog header text
     * @param content the dialog content text
     * @return {@code true} if the user confirmed, {@code false} otherwise
     */
    public boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
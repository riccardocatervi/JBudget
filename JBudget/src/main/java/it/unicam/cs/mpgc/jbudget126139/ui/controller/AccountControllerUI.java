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

import it.unicam.cs.mpgc.jbudget126139.controller.AccountController;
import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.view.AccountsView;
import it.unicam.cs.mpgc.jbudget126139.ui.event.AccountChangedEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.event.UIEventBus;
import it.unicam.cs.mpgc.jbudget126139.ui.service.DialogService;
import it.unicam.cs.mpgc.jbudget126139.ui.viewimpl.AccountsViewImpl;

import java.math.BigDecimal;
import java.util.List;

/**
 * UI controller responsible for managing the accounts section of the application.
 * <p>
 * Handles communication between the {@link AccountController}, {@link TransactionController},
 * and the {@link AccountsView}, while coordinating UI interactions such as
 * creating, editing, and deleting accounts.
 * </p>
 */
public class AccountControllerUI implements UIController {

    private final AccountsView view;
    private final AccountController accountController;
    private final TransactionController transactionController;
    private final UIEventBus eventBus;
    private final DialogService dialogService;

    /**
     * Creates a new {@code AccountControllerUI}.
     *
     * @param view                  the accounts view
     * @param accountController     the controller managing accounts
     * @param transactionController the controller managing transactions
     * @param eventBus              the event bus for publishing UI events
     * @param dialogService         the dialog service for showing user dialogs
     */
    public AccountControllerUI(AccountsView view,
                               AccountController accountController,
                               TransactionController transactionController,
                               UIEventBus eventBus,
                               DialogService dialogService) {
        this.view = view;
        this.accountController = accountController;
        this.transactionController = transactionController;
        this.eventBus = eventBus;
        this.dialogService = dialogService;
    }

    /**
     * Initializes the controller by loading all accounts and updating balances.
     */
    @Override
    public void initialize() {
        if (view instanceof AccountsViewImpl impl) {
            impl.setTransactionController(transactionController);
            impl.setOnAddAccount(this::showAddAccountDialog);
            impl.setOnEditAccount(this::showEditAccountDialog);
            impl.setOnDeleteAccount(this::showDeleteAccountDialog);
        }
        loadAccounts();
    }

    /**
     * Cleans up any resources when this controller is no longer needed.
     * Currently unused, but reserved for future extension.
     */
    @Override
    public void cleanup() {
        // Cleanup if needed
    }

    /**
     * Returns the associated view.
     *
     * @return the {@link AccountsView}
     */
    public AccountsView getView() {
        return view;
    }

    /**
     * Refresh the accounts list with updated balances.
     * This method is called when transactions are added/updated/deleted
     * to ensure account balances are always current.
     */
    public void refreshAccounts() {
        loadAccounts();
    }

    /**
     * Loads all accounts from the backend, displays them in the view,
     * and updates their balances using the {@link TransactionController}.
     */
    private void loadAccounts() {
        try {
            view.showLoading();
            List<AccountDTO> accounts = accountController.listAccounts();
            view.displayAccounts(accounts);

            for (AccountDTO account : accounts) {
                try {
                    BigDecimal balance = transactionController.getAccountBalance(account.id());
                    view.updateAccountBalance(account.id(), balance);
                } catch (Exception e) {
                    System.err.println("Error calculating balance for account " + account.id() + ": " + e.getMessage());
                    view.updateAccountBalance(account.id(), BigDecimal.ZERO);
                }
            }

            view.hideLoading();
        } catch (Exception e) {
            view.showError("Failed to load accounts: " + e.getMessage());
            view.hideLoading();
        }
    }

    /**
     * Shows a dialog for creating a new account. On success, the new account is published
     * as an {@link AccountChangedEvent} and accounts are reloaded in the view.
     */
    public void showAddAccountDialog() {
        dialogService.showAddAccountDialog(
                newAccount -> {
                    eventBus.publish(new AccountChangedEvent(newAccount, AccountChangedEvent.AccountAction.CREATED));
                    loadAccounts();
                    dialogService.showSuccessAlert("AbstractAccount created successfully!");
                },
                view::showError
        );
    }

    /**
     * Shows a dialog for editing an existing account. On success, the updated account is published
     * as an {@link AccountChangedEvent} and accounts are reloaded in the view.
     *
     * @param account the account to edit
     */
    public void showEditAccountDialog(AccountDTO account) {
        dialogService.showEditAccountDialog(account,
                updatedAccount -> {
                    eventBus.publish(new AccountChangedEvent(updatedAccount, AccountChangedEvent.AccountAction.UPDATED));
                    loadAccounts();
                    dialogService.showSuccessAlert("AbstractAccount updated successfully!");
                },
                view::showError
        );
    }

    /**
     * Shows a dialog for deleting an account. On success, the deleted account is published
     * as an {@link AccountChangedEvent} and accounts are reloaded in the view.
     *
     * @param account the account to delete
     */
    public void showDeleteAccountDialog(AccountDTO account) {
        try {
            List<AccountDTO> allAccounts = accountController.listAccounts();

            dialogService.showDeleteAccountDialog(account, allAccounts,
                    () -> {
                        eventBus.publish(new AccountChangedEvent(account, AccountChangedEvent.AccountAction.DELETED));
                        loadAccounts();
                        dialogService.showSuccessAlert("AbstractAccount deleted successfully!");
                    },
                    view::showError
            );
        } catch (Exception e) {
            view.showError("Error during account deletion: " + e.getMessage());
        }
    }
}
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

import it.unicam.cs.mpgc.jbudget126139.controller.*;
import it.unicam.cs.mpgc.jbudget126139.service.RecurrenceService;
import it.unicam.cs.mpgc.jbudget126139.ui.viewimpl.*;
import it.unicam.cs.mpgc.jbudget126139.ui.event.UIEventBus;
import it.unicam.cs.mpgc.jbudget126139.ui.service.DialogService;

/**
 * Factory class responsible for creating and wiring UI controllers with their
 * corresponding views and dependencies.
 * <p>
 * Each method returns a fully initialized UI controller bound to its view,
 * with event handlers already set up.
 * </p>
 */
public class UIControllerFactory {

    private final AccountController accountController;
    private final TransactionController transactionController;
    private final TagController tagController;
    private final RecurrenceController recurrenceController;
    private final RecurrenceService recurrenceService;
    private final DialogService dialogService;
    private final UIEventBus eventBus;

    /**
     * Creates a new {@code UIControllerFactory}.
     *
     * @param accountController     the controller managing accounts
     * @param transactionController the controller managing transactions
     * @param tagController         the controller managing tags
     * @param recurrenceController  the controller managing recurrences
     * @param recurrenceService     the service handling recurrence logic
     * @param eventBus              the UI event bus
     */
    public UIControllerFactory(AccountController accountController,
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

        this.dialogService = new DialogService(accountController, transactionController,
                tagController, recurrenceController, recurrenceService, eventBus);
    }

    /**
     * Creates and initializes the dashboard controller with its view.
     *
     * @return a fully configured {@link DashboardControllerUI}
     */
    public DashboardControllerUI createDashboardController() {
        DashboardViewImpl view = new DashboardViewImpl();
        DashboardControllerUI controller = new DashboardControllerUI(
                view,
                transactionController,
                tagController,
                eventBus,
                dialogService
        );

        view.setOnAddTransaction(controller::handleAddTransaction);
        view.setOnViewAllTransactions(controller::handleViewAllTransactions);
        view.setOnPeriodChange(controller::updateChartPeriod);

        return controller;
    }

    /**
     * Creates and initializes the accounts controller with its view.
     *
     * @return a fully configured {@link AccountControllerUI}
     */
    public AccountControllerUI createAccountsController() {
        AccountsViewImpl view = new AccountsViewImpl();
        AccountControllerUI controller = new AccountControllerUI(
                view,
                accountController,
                transactionController,
                eventBus,
                dialogService
        );

        view.setOnAddAccount(controller::showAddAccountDialog);
        view.setOnEditAccount(controller::showEditAccountDialog);
        view.setOnDeleteAccount(controller::showDeleteAccountDialog);

        return controller;
    }

    /**
     * Creates and initializes the transactions controller with its view.
     *
     * @return a fully configured {@link TransactionControllerUI}
     */
    public TransactionControllerUI createTransactionsController() {
        TransactionsViewImpl view = new TransactionsViewImpl();
        TransactionControllerUI controller = new TransactionControllerUI(
                view,
                transactionController,
                tagController,
                dialogService,
                eventBus
        );

        view.setOnAddTransaction(controller::showAddTransactionDialog);
        view.setOnEditTransaction(controller::showEditTransactionDialog);
        view.setOnDeleteTransaction(controller::showDeleteTransactionDialog);
        view.setOnApplyFilters(controller::applyFilters);
        view.setOnClearFilters(controller::clearFilters);

        return controller;
    }

    /**
     * Creates and initializes the tags controller with its view.
     *
     * @return a fully configured {@link TagControllerUI}
     */
    public TagControllerUI createTagsController() {
        TagsViewImpl view = new TagsViewImpl();
        TagControllerUI controller = new TagControllerUI(view, tagController, dialogService, eventBus);

        view.setController(controller);
        view.setOnAddTag(() -> controller.showAddTagDialog(null));
        view.setOnAddChildTag(controller::showAddTagDialog);
        view.setOnEditTag(controller::showEditTagDialog);
        view.setOnDeleteTag(controller::showDeleteTagDialog);

        return controller;
    }

    /**
     * Creates and initializes the recurring transactions controller with its view.
     *
     * @return a fully configured {@link RecurringControllerUI}
     */
    public RecurringControllerUI createRecurringController() {
        RecurringViewImpl view = new RecurringViewImpl();
        RecurringControllerUI controller = new RecurringControllerUI(
                view,
                recurrenceController,
                transactionController,
                tagController,
                recurrenceService,
                dialogService,
                eventBus
        );

        view.setController(controller);
        view.setOnAddRecurring(controller::showAddRecurringDialog);
        view.setOnEditRecurring(controller::showEditRecurringDialog);
        view.setOnDeleteRecurring(controller::showDeleteRecurringDialog);
        view.setOnViewRecurringTransactions(controller::showRecurringTransactions);

        return controller;
    }

    private DialogService createDialogService() {
        return new DialogService(
                accountController,
                transactionController,
                tagController,
                recurrenceController,
                recurrenceService,
                eventBus
        );
    }
}
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

import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.controller.TagController;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.StatisticsDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.event.*;
import it.unicam.cs.mpgc.jbudget126139.ui.navigation.NavigationTarget;
import it.unicam.cs.mpgc.jbudget126139.ui.view.DashboardView;
import it.unicam.cs.mpgc.jbudget126139.ui.viewimpl.DashboardViewImpl;
import it.unicam.cs.mpgc.jbudget126139.ui.service.DialogService;
import it.unicam.cs.mpgc.jbudget126139.ui.service.CurrencyService;
import javafx.application.Platform;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

/**
 * UI controller responsible for managing the {@link DashboardView}.
 * <p>
 * Handles account selection, dashboard data loading, statistics updates,
 * and user interactions (adding, deleting, and viewing transactions).
 * This controller listens to {@link UIEventBus} events such as
 * {@link AccountSelectedEvent}, {@link TransactionEvent}, and {@link DataRefreshEvent}
 * to keep the dashboard synchronized with the application state.
 * </p>
 */
public class DashboardControllerUI implements UIController {

    /**
     * The dashboard view associated with this controller.
     */
    private final DashboardView view;

    /**
     * Service for managing transactions.
     */
    private final TransactionController transactionController;

    /**
     * Service for managing tags and categories.
     */
    private final TagController tagController;

    /**
     * Event bus for publishing and subscribing to UI events.
     */
    private final UIEventBus eventBus;

    /**
     * Service for showing dialogs and alerts to the user.
     */
    private final DialogService dialogService;

    /**
     * Currently selected account displayed in the dashboard.
     */
    private AccountDTO currentAccount;

    /**
     * Creates a new dashboard controller.
     *
     * @param view                  the dashboard view
     * @param transactionController controller for transaction operations
     * @param tagController         controller for tag/category operations
     * @param eventBus              UI event bus
     * @param dialogService         dialog and alert service
     */
    public DashboardControllerUI(DashboardView view,
                                 TransactionController transactionController,
                                 TagController tagController,
                                 UIEventBus eventBus,
                                 DialogService dialogService) {
        this.view = view;
        this.transactionController = transactionController;
        this.tagController = tagController;
        this.eventBus = eventBus;
        this.dialogService = dialogService;
        setupEventHandlers();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Subscribes to account and transaction events to keep the dashboard updated.
     * </p>
     */
    @Override
    public void initialize() {
        eventBus.subscribe(AccountSelectedEvent.class, this::onAccountSelected);
        eventBus.subscribe(TransactionEvent.class, this::onTransactionChanged);

        eventBus.subscribe(DataRefreshEvent.class, this::onDataRefresh);

        if (currentAccount != null)
            loadDashboardData();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Unsubscribes from events to avoid memory leaks when the controller is disposed.
     * </p>
     */
    @Override
    public void cleanup() {
        eventBus.unsubscribe(AccountSelectedEvent.class, this::onAccountSelected);
        eventBus.unsubscribe(TransactionEvent.class, this::onTransactionChanged);
    }

    /**
     * Returns the dashboard view managed by this controller.
     *
     * @return the {@link DashboardView}
     */
    public DashboardView getView() {
        return view;
    }

    /**
     * Sets the current account to display in the dashboard.
     *
     * @param account the selected account
     */
    public void setCurrentAccount(AccountDTO account) {
        applyAccountSelection(account);
    }

    /**
     * Updates sidebar statistics (balance, income, expenses) for the current account.
     * Publishes a {@link DataRefreshEvent} with updated values.
     */
    public void updateSidebarStats() {
        if (currentAccount == null) return;

        try {
            BigDecimal balance = transactionController.getAccountBalance(currentAccount.id());

            OffsetDateTime monthStart = OffsetDateTime.now().withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
            OffsetDateTime now = OffsetDateTime.now();

            BigDecimal income = transactionController.calculateIncome(currentAccount.id(), monthStart, now);
            BigDecimal expenses = transactionController.calculateExpenses(currentAccount.id(), monthStart, now);

            eventBus.publish(new DataRefreshEvent(DataRefreshEvent.DataType.STATS,
                    Map.of("balance", balance, "income", income, "expenses", expenses, "account", currentAccount)));

        } catch (Exception e) {
            view.showError("Failed to update dashboard: " + e.getMessage());
        }
    }

    /**
     * Loads and refreshes dashboard data such as balance, income, expenses,
     * charts, and recent transactions.
     */
    public void loadDashboardData() {
        if (currentAccount == null) {
            view.showEmptyState();
            return;
        }

        try {
            view.showLoading();

            Platform.runLater(() -> {
                try {
                    OffsetDateTime monthStart = OffsetDateTime.now().withDayOfMonth(1)
                            .withHour(0).withMinute(0).withSecond(0).withNano(0);
                    OffsetDateTime monthEnd = OffsetDateTime.now();

                    if (transactionController == null) {
                        view.showError("Transaction controller not initialized");
                        view.hideLoading();
                        return;
                    }

                    StatisticsDTO stats = transactionController.getAccountStatistics(
                            currentAccount.id(), monthStart, monthEnd);

                    view.updateBalance(stats.totalBalance());
                    view.updateIncomeExpenses(stats.totalIncome(), stats.totalExpenses());

                    if (view instanceof DashboardViewImpl impl) {
                        impl.updateExpenseChart(stats.totalIncome(), stats.totalExpenses(), "This Month");
                        impl.updateCategoryChart(stats.spendingByCategory());
                    }

                    view.updateRecentTransactions(stats.recentTransactions());
                    view.hideLoading();

                } catch (Exception e) {
                    view.showError("Failed to load dashboard data: " + e.getMessage());
                    view.hideLoading();
                }
            });

        } catch (Exception e) {
            view.showError("Failed to load dashboard data: " + e.getMessage());
            view.hideLoading();
        }
    }

    /**
     * Handles the "Add Transaction" action, showing a dialog for transaction creation
     * and refreshing the dashboard once the operation is completed.
     */
    public void handleAddTransaction() {
        if (currentAccount == null) {
            dialogService.showErrorAlert("Please select an account first.");
            return;
        }

        try {
            List<TagDTO> rootTags = tagController.listRootTags();
            List<TagDTO> allTags = new ArrayList<>(rootTags);

            for (TagDTO rootTag : rootTags) {
                allTags.addAll(loadChildTagsRecursively(rootTag));
            }
            dialogService.showAddTransactionDialog(
                    currentAccount,
                    allTags,
                    transaction -> {
                        eventBus.publish(new TransactionChangedEvent(
                                transaction,
                                TransactionChangedEvent.TransactionAction.ADDED,
                                currentAccount.id()
                        ));

                        dialogService.showSuccessAlert("Transaction added successfully!");
                        updateSidebarStats();
                        loadDashboardData();
                    },
                    dialogService::showErrorAlert
            );
        } catch (Exception e) {
            dialogService.showErrorAlert("Failed to load categories: " + e.getMessage());
        }
    }

    /**
     * Navigates to the "All Transactions" view.
     */
    public void handleViewAllTransactions() {
        eventBus.publish(new NavigationEvent(NavigationTarget.TRANSACTIONS));
    }

    /**
     * Updates charts according to the selected period (e.g., "This Month", "Last 3 Months").
     *
     * @param period the selected period
     */
    public void updateChartPeriod(String period) {
        if (currentAccount == null) return;

        try {
            OffsetDateTime cutoffDate = getCutoffDateForPeriod(period);
            OffsetDateTime now = OffsetDateTime.now();

            StatisticsDTO stats = transactionController.getAccountStatistics(
                    currentAccount.id(), cutoffDate, now);

            if (view instanceof DashboardViewImpl impl) {
                impl.updateExpenseChart(stats.totalIncome(), stats.totalExpenses(), period);
                impl.updateCategoryChart(stats.spendingByCategory());
            }
        } catch (Exception e) {
            view.showError("Failed to update chart: " + e.getMessage());
        }
    }

    // Private helper methods

    private void onTransactionChanged(TransactionEvent event) {
        loadDashboardData();
        updateSidebarStats();
    }

    private void onDataRefresh(DataRefreshEvent event) {
        if (event.type() == DataRefreshEvent.DataType.TRANSACTIONS) {
            loadDashboardData();
            updateSidebarStats();
        }
    }

    private void onAccountSelected(AccountSelectedEvent e) {
        applyAccountSelection(e.account());
    }

    private void applyAccountSelection(AccountDTO account) {
        if (account == null) return;

        this.currentAccount = account;
        CurrencyService.getInstance().setAccountCurrency(account);

        if (view instanceof DashboardViewImpl impl) {
            impl.updateAccountInfo(account);
            impl.setCurrency(Currency.getInstance(String.valueOf(account.currency())));
            impl.hideEmptyState();
        }
        loadDashboardData();
        updateSidebarStats();
    }

    private void setupEventHandlers() {
        if (view instanceof DashboardViewImpl impl) {
            impl.setOnAddTransaction(this::handleAddTransaction);
            impl.setOnViewAllTransactions(this::handleViewAllTransactions);
            impl.setOnPeriodChange(this::updateChartPeriod);
        }
    }

    private List<TagDTO> loadChildTagsRecursively(TagDTO parent) {
        List<TagDTO> result = new ArrayList<>();
        try {
            List<TagDTO> children = tagController.listChildTags(parent.id());
            for (TagDTO child : children) {
                result.add(child);
                result.addAll(loadChildTagsRecursively(child));
            }
        } catch (Exception e) { }
        return result;
    }

    private OffsetDateTime getCutoffDateForPeriod(String period) {
        OffsetDateTime now = OffsetDateTime.now();
        return switch (period) {
            case "Last 3 Months" -> now.minusMonths(3).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "Last 6 Months" -> now.minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            default -> now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        };
    }
}
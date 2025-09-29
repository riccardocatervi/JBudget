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
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TagDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionFilterDTO;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.ui.event.AccountSelectedEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.event.TransactionChangedEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.event.DataRefreshEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.view.TransactionsView;
import it.unicam.cs.mpgc.jbudget126139.ui.service.DialogService;
import it.unicam.cs.mpgc.jbudget126139.ui.event.UIEventBus;
import it.unicam.cs.mpgc.jbudget126139.ui.event.TransactionEvent;
import it.unicam.cs.mpgc.jbudget126139.ui.viewimpl.TransactionsViewImpl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * UI controller responsible for managing the transactions view.
 * <p>
 * Handles account selection, loading and filtering transactions,
 * and coordinating dialogs for creating, editing, and deleting transactions.
 * </p>
 */
public class TransactionControllerUI implements UIController {

    private final TransactionsView view;
    private final TransactionController transactionController;
    private final TagController tagController;
    private final DialogService dialogService;
    private final UIEventBus eventBus;

    private AccountDTO currentAccount;

    /**
     * Creates a new {@code TransactionControllerUI}.
     *
     * @param view                  the transactions view
     * @param transactionController the controller managing transactions
     * @param tagController         the controller managing tags/categories
     * @param dialogService         the dialog service for user prompts
     * @param eventBus              the UI event bus
     */
    public TransactionControllerUI(TransactionsView view,
                                   TransactionController transactionController,
                                   TagController tagController,
                                   DialogService dialogService,
                                   UIEventBus eventBus) {
        this.view = view;
        this.transactionController = transactionController;
        this.tagController = tagController;
        this.dialogService = dialogService;
        this.eventBus = eventBus;
    }

    /**
     * Initializes the controller by subscribing to UI events, wiring view callbacks,
     * loading available filters, and displaying the initial list of transactions.
     */
    @Override
    public void initialize() {
        eventBus.subscribe(AccountSelectedEvent.class, this::onAccountSelected);
        eventBus.subscribe(DataRefreshEvent.class, this::onDataRefresh);
        loadFilters();

        if (view instanceof TransactionsViewImpl impl) {
            impl.setOnAddTransaction(this::showAddTransactionDialog);
            impl.setOnEditTransaction(this::showEditTransactionDialog);
            impl.setOnDeleteTransaction(this::showDeleteTransactionDialog);
            impl.setOnApplyFilters(this::applyFilters);
            impl.setOnClearFilters(this::clearFilters);
            impl.setTagController(tagController);
        }

        loadTransactions();
    }

    private void onAccountSelected(AccountSelectedEvent event) {
        setCurrentAccount(event.account());
    }

    private void onDataRefresh(DataRefreshEvent event) {
        if (event.type() == DataRefreshEvent.DataType.TRANSACTIONS || event.type() == DataRefreshEvent.DataType.ALL) {
            loadTransactions();
        }
    }

    /**
     * Cleans up controller resources by unsubscribing from UI events.
     */
    @Override
    public void cleanup() {
        eventBus.unsubscribe(AccountSelectedEvent.class, this::onAccountSelected);
    }

    /**
     * Returns the associated transactions view.
     *
     * @return the {@link TransactionsView} managed by this controller
     */
    public TransactionsView getView() {
        return view;
    }

    /**
     * Sets the currently selected account and refreshes the transactions view.
     * If the view implementation supports currency display, it updates the currency as well.
     *
     * @param account the account to set as current; if {@code null}, the view will be cleared
     */
    public void setCurrentAccount(AccountDTO account) {
        this.currentAccount = account;
        if (view instanceof TransactionsViewImpl impl && account != null)
            impl.setCurrency(account.currency());
        loadTransactions();
    }

    private void loadFilters() {
        try {
            List<TagDTO> tags = tagController.listRootTags();
            List<TagDTO> allTags = getAllTagsRecursively(tags);
            view.updateFilters(allTags);
        } catch (Exception e) {
            view.showError("Failed to load filters: " + e.getMessage());
        }
    }

    private List<TagDTO> getAllTagsRecursively(List<TagDTO> rootTags) {
        List<TagDTO> allTags = new ArrayList<>(rootTags);
        for (TagDTO rootTag : rootTags) {
            addChildTagsRecursively(rootTag, allTags);
        }
        return allTags;
    }

    private void addChildTagsRecursively(TagDTO parentTag, List<TagDTO> allTags) {
        try {
            List<TagDTO> children = tagController.listChildTags(parentTag.id());
            for (TagDTO child : children) {
                if (!allTags.stream().anyMatch(tag -> tag.id().equals(child.id()))) {
                    allTags.add(child);
                    addChildTagsRecursively(child, allTags);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading children for tag " + parentTag.id() + ": " + e.getMessage());
        }
    }

    private void loadTransactions() {
        if (currentAccount == null) {
            view.displayTransactions(List.of());
            return;
        }

        try {
            view.showLoading();

            TransactionFilterDTO emptyFilter = TransactionFilterDTO.empty();
            List<TransactionDTO> transactions = transactionController.searchTransactions(
                    currentAccount.id(), emptyFilter, 0, 1000);

            view.displayTransactions(transactions);
            view.hideLoading();
        } catch (Exception e) {
            view.showError("Failed to load transactions: " + e.getMessage());
            view.hideLoading();
        }
    }

    /**
     * Opens the dialog to create a new transaction for the current account.
     * On successful creation, publishes a {@link TransactionEvent} and refreshes the view.
     * If no account is selected, shows an error alert.
     */
    public void showAddTransactionDialog() {
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
            dialogService.showAddTransactionDialog(currentAccount, allTags,
                    transaction -> {
                        eventBus.publish(new TransactionChangedEvent(
                                transaction,
                                TransactionChangedEvent.TransactionAction.ADDED,
                                currentAccount.id()
                        ));

                        loadTransactions();
                        dialogService.showSuccessAlert("Transaction added successfully!");
                    },
                    view::showError
            );
        } catch (Exception e) {
            view.showError("Failed to open add transaction dialog: " + e.getMessage());
        }
    }

    /**
     * Opens the dialog to edit an existing transaction.
     * On successful update, publishes a {@link TransactionEvent} and refreshes the view.
     *
     * @param transaction the transaction to edit; must not be {@code null}
     */
    public void showEditTransactionDialog(TransactionDTO transaction) {
        try {
            List<TagDTO> rootTags = tagController.listRootTags();
            List<TagDTO> allTags = new ArrayList<>(rootTags);

            for (TagDTO rootTag : rootTags) {
                allTags.addAll(loadChildTagsRecursively(rootTag));
            }
            dialogService.showEditTransactionDialog(transaction, currentAccount, allTags,
                    updatedTransaction -> {
                        eventBus.publish(new TransactionChangedEvent(
                                updatedTransaction,
                                TransactionChangedEvent.TransactionAction.UPDATED,
                                currentAccount.id()
                        ));

                        loadTransactions();
                        dialogService.showSuccessAlert("Transaction updated successfully!");
                    },
                    view::showError
            );

        } catch (Exception e) {
            view.showError("Failed to open edit transaction dialog: " + e.getMessage());
        }
    }

    /**
     * Opens the dialog to confirm deletion of a transaction.
     * On confirmation, publishes a {@link TransactionEvent} and refreshes the view.
     *
     * @param transaction the transaction to delete; must not be {@code null}
     */
    public void showDeleteTransactionDialog(TransactionDTO transaction) {
        dialogService.showDeleteTransactionDialog(transaction, currentAccount,
                () -> {
                    eventBus.publish(new TransactionChangedEvent(
                            transaction,
                            TransactionChangedEvent.TransactionAction.DELETED,
                            currentAccount.id()
                    ));

                    loadTransactions();
                    dialogService.showSuccessAlert("Transaction deleted successfully!");
                },
                view::showError
        );
    }

    /**
     * Applies the provided filter data to the current account's transactions
     * and updates the view with the filtered results.
     *
     * @param filterData the filter parameters coming from the UI
     */
    public void applyFilters(TransactionsViewImpl.FilterData filterData) {
        if (currentAccount == null) {
            return;
        }

        try {
            List<TransactionDTO> filteredTransactions;

            if (!filterData.category.equals("All Categories")) {
                String cleanCategoryName = filterData.category.trim();

                try {
                    List<TagDTO> allTags = getAllTagsRecursively(tagController.listRootTags());
                    UUID categoryId = allTags.stream()
                            .filter(tag -> tag.name().equals(cleanCategoryName))
                            .map(TagDTO::id)
                            .findFirst()
                            .orElse(null);

                    if (categoryId != null) {
                        filteredTransactions = transactionController.listByTag(currentAccount.id(), categoryId);
                        filteredTransactions = applyAdditionalFilters(filteredTransactions, filterData);
                    } else {
                        filteredTransactions = List.of();
                    }
                } catch (Exception e) {
                    System.err.println("Error finding category: " + e.getMessage());
                    filteredTransactions = List.of();
                }
            } else {
                TransactionFilterDTO filter = createFilterFromData(filterData);
                filteredTransactions = transactionController.searchTransactions(
                        currentAccount.id(), filter, 0, 1000);
            }

            view.displayTransactions(filteredTransactions);

        } catch (Exception e) {
            view.showError("Failed to apply filters: " + e.getMessage());
        }
    }

    private List<TransactionDTO> applyAdditionalFilters(List<TransactionDTO> transactions,
                                                        TransactionsViewImpl.FilterData filterData) {
        return transactions.stream()
                .filter(t -> {
                    if (!filterData.type.equals("All Types")) {
                        TransactionDirection expectedDirection = filterData.type.equals("Income") ?
                                TransactionDirection.CREDIT : TransactionDirection.DEBIT;
                        if (t.direction() != expectedDirection) return false;
                    }

                    if (filterData.startDate != null)
                        if (t.valueDate().toLocalDate().isBefore(filterData.startDate)) return false;
                    if (filterData.endDate != null)
                        if (t.valueDate().toLocalDate().isAfter(filterData.endDate)) return false;

                    return true;
                })
                .toList();
    }

    /**
     * Clears any active filters in the view and reloads transactions for the current account.
     */
    public void clearFilters() {
        view.clearFilters();
        loadTransactions();
    }

    private TransactionFilterDTO createFilterFromData(TransactionsViewImpl.FilterData filterData) {
        TransactionDirection direction = null;
        if (!filterData.type.equals("All Types")) {
            direction = filterData.type.equals("Income") ?
                    TransactionDirection.CREDIT : TransactionDirection.DEBIT;
        }

        Set<UUID> tagIds = null;
        if (!filterData.category.equals("All Categories")) {
            String cleanCategoryName = filterData.category.trim();
            try {
                List<TagDTO> allTags = getAllTagsRecursively(tagController.listRootTags());
                UUID categoryId = allTags.stream()
                        .filter(tag -> tag.name().equals(cleanCategoryName))
                        .map(TagDTO::id)
                        .findFirst()
                        .orElse(null);

                if (categoryId != null)
                    tagIds = Set.of(categoryId);
            } catch (Exception e) {
                System.err.println("Error finding category: " + e.getMessage());
            }
        }

        OffsetDateTime fromDate = filterData.startDate != null ?
                filterData.startDate.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime toDate = filterData.endDate != null ?
                filterData.endDate.atTime(23, 59, 59).atOffset(ZoneOffset.UTC) : null;

        return new TransactionFilterDTO(direction, tagIds, fromDate, toDate, null);
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
}
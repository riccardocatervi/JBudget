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

import it.unicam.cs.mpgc.jbudget126139.controller.TransactionController;
import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.component.AccountCard;
import it.unicam.cs.mpgc.jbudget126139.ui.component.ComponentFactory;
import it.unicam.cs.mpgc.jbudget126139.ui.view.AccountsView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Concrete implementation of {@link AccountsView} that renders a grid of accounts
 * with their balances and exposes UI actions for creating, editing, and deleting accounts.
 * <p>
 * The view shows an "Add New AbstractAccount" button, a responsive container with account cards,
 * and integrates with a {@link TransactionController} to compute displayed balances.
 * </p>
 */
public class AccountsViewImpl extends BaseViewImpl implements AccountsView {

    private VBox root;
    private HBox accountsContainer;
    private TransactionController transactionController;

    private Runnable onAddAccount;
    private Consumer<AccountDTO> onEditAccount;
    private Consumer<AccountDTO> onDeleteAccount;

    /**
     * Creates a new {@code AccountsViewImpl} and initializes the UI structure.
     */
    public AccountsViewImpl() {
        initialize();
    }

    private void initialize() {
        initializeBaseComponents();
        root = new VBox(30);
        root.getStyleClass().add("accounts-container");

        createHeader();
        createAccountsContainer();

        StackPane containerWithOverlays = new StackPane();
        containerWithOverlays.getChildren().addAll(root, getLoadingOverlay(), getErrorContainer());

        this.root = new VBox();
        this.root.getChildren().add(containerWithOverlays);
    }

    private void createHeader() {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("My Accounts");
        title.getStyleClass().add("page-title");

        Button addButton = new Button("+ Add New AbstractAccount");
        addButton.getStyleClass().add("add-account-btn");
        addButton.setOnAction(e -> {
            if (onAddAccount != null) onAddAccount.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, addButton);
        root.getChildren().add(header);
    }

    private void createAccountsContainer() {
        accountsContainer = new HBox(20);
        accountsContainer.setPadding(new Insets(10));
        accountsContainer.getStyleClass().add("accounts-grid");
        root.getChildren().add(accountsContainer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Parent getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayAccounts(List<AccountDTO> accounts) {
        accountsContainer.getChildren().clear();
        if (accounts.isEmpty()) {
            Label emptyLabel = new Label("No accounts yet. Create your first account!");
            emptyLabel.getStyleClass().add("empty-state");
            accountsContainer.getChildren().add(emptyLabel);
        } else {
            for (AccountDTO account : accounts) {
                BigDecimal balance = calculateBalanceForAccount(account.id());

                AccountCard card = ComponentFactory.createAccountCard(
                        account,
                        balance,
                        this::handleEditAccount,
                        this::handleDeleteAccount
                );
                accountsContainer.getChildren().add(card);
            }
        }
    }

    private BigDecimal calculateBalanceForAccount(UUID accountId) {
        if (transactionController == null) {
            System.err.println("TransactionController is null in AccountsViewImpl");
            return BigDecimal.ZERO;
        }

        try {
            List<TransactionDTO> accountTransactions = transactionController.listTransactions(accountId);

            return accountTransactions.stream()
                    .map(t -> t.direction() == TransactionDirection.CREDIT ? t.amount() : t.amount().negate())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            System.err.println("Error calculating balance for account " + accountId + ": " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccountBalance(UUID accountId, BigDecimal balance) {
        accountsContainer.getChildren().stream()
                .filter(node -> node instanceof AccountCard)
                .map(node -> (AccountCard) node)
                .filter(card -> card.getAccount().id().equals(accountId))
                .findFirst()
                .ifPresent(card -> card.updateBalance(balance));
    }


    private void handleEditAccount(AccountDTO account) {
        if (onEditAccount != null)
            onEditAccount.accept(account);
    }

    private void handleDeleteAccount(AccountDTO account) {
        if (onDeleteAccount != null)
            onDeleteAccount.accept(account);
    }

    /**
     * Sets the handler invoked when the user clicks the "Add New AbstractAccount" button.
     *
     * @param handler a {@link Runnable} to execute when adding an account
     */
    public void setOnAddAccount(Runnable handler) {
        this.onAddAccount = handler;
    }

    /**
     * Sets the handler invoked when an account edit action is requested from a card.
     *
     * @param handler a consumer receiving the selected {@link AccountDTO}
     */
    public void setOnEditAccount(Consumer<AccountDTO> handler) {
        this.onEditAccount = handler;
    }

    /**
     * Sets the handler invoked when an account delete action is requested from a card.
     *
     * @param handler a consumer receiving the selected {@link AccountDTO}
     */
    public void setOnDeleteAccount(Consumer<AccountDTO> handler) {
        this.onDeleteAccount = handler;
    }

    /**
     * Injects the {@link TransactionController} used to compute account balances displayed in the view.
     *
     * @param transactionController the transaction controller instance
     */
    public void setTransactionController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }
}
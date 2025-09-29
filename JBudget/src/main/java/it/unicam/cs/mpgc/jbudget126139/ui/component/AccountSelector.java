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

package it.unicam.cs.mpgc.jbudget126139.ui.component;

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.function.Consumer;

/**
 * UI component that provides a selector for choosing an {@link AccountDTO}.
 * <p>
 * Displays a dropdown menu of available accounts, allows switching the
 * currently selected account, and provides a button for adding new accounts.
 * </p>
 */
public class AccountSelector extends HBox {

    private final ComboBox<AccountDTO> accountCombo;
    private final ObservableList<AccountDTO> accounts = FXCollections.observableArrayList();
    private final Consumer<AccountDTO> onAccountChange;
    private AccountDTO currentAccount;

    /**
     * Constructs a new {@code AccountSelector}.
     *
     * @param onAccountChange the callback executed when the selected account changes
     */
    public AccountSelector(Consumer<AccountDTO> onAccountChange) {
        this.onAccountChange = onAccountChange;
        this.accountCombo = new ComboBox<>(accounts);
        initialize();
    }

    /**
     * Initializes the selector layout, dropdown, and action button.
     */
    private void initialize() {
        setSpacing(15);
        setAlignment(Pos.CENTER_RIGHT);

        Label label = new Label("AbstractAccount:");
        label.getStyleClass().add("account-label");

        accountCombo.getStyleClass().add("modern-account-selector");
        accountCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(AccountDTO account) {
                return account != null ? account.name() : "Select AbstractAccount";
            }

            @Override
            public AccountDTO fromString(String string) {
                return null;
            }
        });

        accountCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            currentAccount = newVal;
            if (onAccountChange != null) onAccountChange.accept(newVal);
        });

        Button addAccountBtn = new Button("+");
        addAccountBtn.getStyleClass().add("add-account-btn");
        addAccountBtn.setOnAction(e -> handleAddAccount());

        getChildren().addAll(label, accountCombo, addAccountBtn);
    }

    /**
     * Updates the list of accounts displayed in the dropdown
     * and preserves the previously selected account if possible.
     *
     * @param accountList the list of accounts to display
     */
    public void updateAccounts(List<AccountDTO> accountList) {
        AccountDTO selectedAccount = currentAccount;
        accounts.clear();
        accounts.addAll(accountList);

        if (selectedAccount != null) {
            currentAccount = accounts.stream()
                    .filter(acc -> acc.id().equals(selectedAccount.id()))
                    .findFirst()
                    .orElse(accountList.isEmpty() ? null : accountList.get(0));
        } else if (!accountList.isEmpty())
            currentAccount = accountList.get(0);

        accountCombo.setValue(currentAccount);

        accountCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            currentAccount = newVal;
            if (onAccountChange != null) onAccountChange.accept(newVal);
        });
    }

    /**
     * Sets the current account and updates the selector's displayed value.
     *
     * @param account the account to set as selected
     */
    public void setCurrentAccount(AccountDTO account) {
        this.currentAccount = account;
        if (!account.equals(accountCombo.getValue())) {
            accountCombo.setValue(account);
        }
    }

    /**
     * Returns the currently selected account.
     *
     * @return the selected account, or {@code null} if none is selected
     */
    public AccountDTO getCurrentAccount() {
        return currentAccount;
    }

    /**
     * Handles the "Add account" action.
     * <p>
     * Currently implemented as a placeholder that logs to the console.
     * In the future, this should open a dialog or trigger an account creation flow.
     * </p>
     */
    private void handleAddAccount() {
        System.out.println("Add account requested");
    }
}
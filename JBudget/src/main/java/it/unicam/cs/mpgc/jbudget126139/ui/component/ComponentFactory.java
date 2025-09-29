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
import it.unicam.cs.mpgc.jbudget126139.service.dto.TransactionDTO;
import it.unicam.cs.mpgc.jbudget126139.ui.service.CurrencyService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Factory class for creating UI components related to accounts,
 * transactions, and statistics.
 * <p>
 * Centralizes component creation logic to ensure consistent initialization
 * and reduce duplication across the UI layer.
 * </p>
 */
public class ComponentFactory {

    /**
     * Creates a new {@link AccountCard} component.
     *
     * @param account  the account data
     * @param balance  the current balance for the account
     * @param onEdit   callback executed when the edit action is triggered
     * @param onDelete callback executed when the delete action is triggered
     * @return a configured {@link AccountCard} instance
     */
    public static AccountCard createAccountCard(AccountDTO account,
                                                BigDecimal balance,
                                                Consumer<AccountDTO> onEdit,
                                                Consumer<AccountDTO> onDelete) {
        return new AccountCard(account, balance, onEdit, onDelete);
    }

    /**
     * Creates a {@link TransactionItem} with the application's default currency.
     *
     * @param transaction the transaction data
     * @return a configured {@link TransactionItem} instance
     */
    public static TransactionItem createTransactionItem(TransactionDTO transaction) {
        Currency currency = CurrencyService.getInstance().getDefaultCurrency();
        return new TransactionItem(transaction, currency);
    }

    /**
     * Creates a {@link TransactionItem} with the specified currency.
     *
     * @param transaction the transaction data
     * @param currency    the currency to display amounts in
     * @return a configured {@link TransactionItem} instance
     */
    public static TransactionItem createTransactionItem(TransactionDTO transaction, Currency currency) {
        return new TransactionItem(transaction, currency);
    }

    /**
     * Creates a {@link TransactionItem} using the currency of a given account.
     *
     * @param transaction the transaction data
     * @param accountId   the ID of the account whose currency should be used
     * @return a configured {@link TransactionItem} instance
     */
    public static TransactionItem createTransactionItem(TransactionDTO transaction, UUID accountId) {
        Currency currency = CurrencyService.getInstance().getAccountCurrency(accountId);
        return new TransactionItem(transaction, currency);
    }

    /**
     * Creates a {@link StatCard} for displaying statistics in the UI.
     *
     * @param title    the card title
     * @param value    the main value to display
     * @param subtitle an optional subtitle for additional context
     * @return a configured {@link StatCard} instance
     */
    public static StatCard createStatCard(String title, String value, String subtitle) {
        return new StatCard(title, value, subtitle);
    }
}
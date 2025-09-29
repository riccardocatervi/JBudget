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

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import java.util.Currency;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * Singleton service for managing currencies associated with accounts.
 * <p>
 * Maintains a thread-safe mapping between account IDs and their
 * {@link Currency}, with a configurable default currency used when
 * no mapping is found.
 * </p>
 * <p>
 * This service is typically used by UI controllers (e.g. dashboard)
 * to ensure that transactions and balances are displayed in the
 * correct currency for each account.
 * </p>
 */
public class CurrencyService {

    private static CurrencyService instance;
    private final ConcurrentHashMap<UUID, Currency> accountCurrencies = new ConcurrentHashMap<>();
    private Currency defaultCurrency = Currency.getInstance("USD");

    private CurrencyService() {}

    /**
     * Returns the singleton instance of {@code CurrencyService}.
     * <p>
     * Uses double-checked locking to ensure thread-safe initialization.
     * </p>
     *
     * @return the singleton instance
     */
    public static CurrencyService getInstance() {
        if (instance == null) {
            synchronized (CurrencyService.class) {
                if (instance == null) {
                    instance = new CurrencyService();
                }
            }
        }
        return instance;
    }

    /**
     * Associates a specific currency with the given account ID.
     *
     * @param accountId the unique identifier of the account
     * @param currency  the currency to assign
     */
    public void setAccountCurrency(UUID accountId, Currency currency) {
        accountCurrencies.put(accountId, currency);
    }

    /**
     * Associates the currency from the given {@link AccountDTO}
     * with its account ID.
     *
     * @param account the account whose currency will be stored
     */
    public void setAccountCurrency(AccountDTO account) {
        accountCurrencies.put(account.id(), account.currency());
    }

    /**
     * Retrieves the currency for the specified account ID.
     * If no mapping exists, the default currency is returned.
     *
     * @param accountId the account ID
     * @return the assigned currency, or the default if none is set
     */
    public Currency getAccountCurrency(UUID accountId) {
        return accountCurrencies.getOrDefault(accountId, defaultCurrency);
    }

    /**
     * Updates the system-wide default currency.
     * This currency is used as a fallback when no account-specific
     * currency is defined.
     *
     * @param currency the new default currency
     */
    public void setDefaultCurrency(Currency currency) {
        this.defaultCurrency = currency;
    }

    /**
     * Returns the currently configured default currency.
     *
     * @return the default currency
     */
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    /**
     * Removes the currency mapping for a specific account ID.
     *
     * @param accountId the account ID to clear
     */
    public void clearAccountCurrency(UUID accountId) {
        accountCurrencies.remove(accountId);
    }

    /**
     * Clears all account-specific currency mappings.
     * The default currency remains unchanged.
     */
    public void clearAll() {
        accountCurrencies.clear();
    }
}
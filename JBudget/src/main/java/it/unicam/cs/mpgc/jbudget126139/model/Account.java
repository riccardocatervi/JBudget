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

package it.unicam.cs.mpgc.jbudget126139.model;

import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a financial account in the budgeting system.
 * An account groups transactions and is associated with a specific currency.
 * Each account has a unique identifier, a name, a creation timestamp,
 * and may contain an optional description.
 */
public interface Account extends Identified<UUID> {

    /**
     * Returns the date and time when this account was created.
     *
     * @return the creation timestamp of this account
     */
    OffsetDateTime getCreatedAt();

    /**
     * Returns the name of this account.
     * The name is intended to uniquely identify the account within the user’s scope.
     *
     * @return the account name
     */
    String getName();

    /**
     * Returns the currency associated with this account.
     * All transactions within this account are expressed in this currency.
     *
     * @return the account currency
     */
    Currency getCurrency();

    /**
     * Returns the description of this account.
     * The description may provide additional context or details about the account’s purpose.
     *
     * @return the account description, or {@code null} if not provided
     */
    String getDescription();

    /**
     * Returns the set of transactions linked to this account.
     * The returned set may be empty if no transactions are recorded.
     *
     * @return a set of transactions associated with this account
     */
    Set<? extends Transaction> getTransactions();
}
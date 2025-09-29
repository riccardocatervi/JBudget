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

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a financial movement (income or expense) associated with an {@link AbstractAccount}.
 * <p>
 * A transaction stores its monetary amount, direction, value date, and
 * optional descriptive details, along with a set of tags for categorization.
 * </p>
 */
public interface Transaction extends Identified<UUID> {

    /**
     * Returns the timestamp indicating when this transaction was created.
     *
     * @return the creation date and time of the transaction; never {@code null}.
     */
    OffsetDateTime getCreatedAt();

    /**
     * Returns the date when the transaction takes effect (value date).
     *
     * @return the value date; never {@code null}.
     */
    OffsetDateTime getValueDate();

    /**
     * Returns the monetary amount of the transaction.
     * <p>
     * This value should be positive and is interpreted in combination with
     * {@link #getDirection()} to determine whether it represents an income or an expense.
     * </p>
     *
     * @return the transaction amount; never {@code null}.
     */
    BigDecimal getAmount();

    /**
     * Returns the direction of the transaction (income or expense).
     *
     * @return the transaction direction; never {@code null}.
     */
    TransactionDirection getDirection();

    /**
     * Returns the description of the transaction, if any.
     *
     * @return the transaction description, or {@code null} if none is set.
     */
    String getDescription();

    /**
     * Returns the account associated with this transaction.
     *
     * @return the associated {@link AbstractAccount}; never {@code null}.
     */
    AbstractAccount getAccount();

    /**
     * Returns the set of tag identifiers associated with this transaction.
     *
     * @return an unmodifiable set of tag UUIDs; never {@code null}, may be empty.
     */
    Set<UUID> getTags();
}
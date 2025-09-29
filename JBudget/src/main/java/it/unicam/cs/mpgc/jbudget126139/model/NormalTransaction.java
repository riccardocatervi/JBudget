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

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Concrete implementation of a {@link Transaction} based on {@link AbstractTransaction},
 * representing a standard, user-created financial transaction.
 * <p>
 * Stored in the {@code transactions} table, with an optional reference
 * to a recurrence pattern through {@link #recurrenceId}.
 * </p>
 */
@Entity
@Table(name = "transactions")
@Access(AccessType.FIELD)
public final class NormalTransaction extends AbstractTransaction {

    /**
     * Identifier of the recurrence associated with this transaction, if any.
     * <p>
     * May be {@code null} if the transaction is not part of a recurring schedule.
     * </p>
     */
    @Column(name = "recurrence_id")
    private UUID recurrenceId;

    /**
     * Protected no-args constructor for JPA.
     */
    public NormalTransaction() {
    }

    /**
     * Creates a new transaction without a recurrence reference.
     *
     * @param valueDate   the transaction value date; must not be {@code null}.
     * @param amount      the transaction amount; must not be {@code null}.
     * @param direction   the transaction direction; must not be {@code null}.
     * @param account     the account associated with this transaction; must not be {@code null}.
     * @param description the optional transaction description; may be {@code null}.
     * @param tags        the set of associated tag IDs; may be {@code null}, in which case an empty set is used.
     */
    public NormalTransaction(OffsetDateTime valueDate,
                             BigDecimal amount,
                             TransactionDirection direction,
                             AbstractAccount account,
                             String description,
                             Set<UUID> tags) {
        super(valueDate, amount, direction, account, description, tags);
    }

    /**
     * Creates a new transaction with an associated recurrence reference.
     *
     * @param valueDate    the transaction value date; must not be {@code null}.
     * @param amount       the transaction amount; must not be {@code null}.
     * @param direction    the transaction direction; must not be {@code null}.
     * @param account      the account associated with this transaction; must not be {@code null}.
     * @param description  the optional transaction description; may be {@code null}.
     * @param tags         the set of associated tag IDs; may be {@code null}, in which case an empty set is used.
     * @param recurrenceId the identifier of the associated recurrence; may be {@code null}.
     */
    public NormalTransaction(OffsetDateTime valueDate,
                             BigDecimal amount,
                             TransactionDirection direction,
                             AbstractAccount account,
                             String description,
                             Set<UUID> tags,
                             UUID recurrenceId) {
        super(valueDate, amount, direction, account, description, tags);
        this.recurrenceId = recurrenceId;
    }

    /**
     * Returns the identifier of the recurrence associated with this transaction, if any.
     *
     * @return the recurrence identifier, or {@code null} if none is set.
     */
    public UUID getRecurrenceId() {
        return recurrenceId;
    }

    /**
     * Updates the recurrence identifier associated with this transaction.
     *
     * @param recurrenceId the new recurrence identifier; may be {@code null}.
     */
    public void setRecurrenceId(UUID recurrenceId) {
        this.recurrenceId = recurrenceId;
    }
}
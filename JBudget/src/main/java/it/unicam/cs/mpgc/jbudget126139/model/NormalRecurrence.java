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
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Standard implementation of a {@link Recurrence} entity,
 * representing a scheduled, repeating transaction pattern
 * associated with an {@link AbstractAccount}.
 * <p>
 * Stores the start date, optional end date, and the recurrence frequency.
 * </p>
 *
 * <p>
 * Instances are persisted in the {@code recurrences} table.
 * </p>
 */
@Entity
@Table(name = "recurrences")
@Access(AccessType.FIELD)
public class NormalRecurrence implements Recurrence {

    /**
     * Unique identifier of the recurrence, generated automatically using {@link GenerationType#UUID}.
     * <p>
     * Immutable after creation.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Timestamp indicating when the recurrence was created.
     * Automatically set by the persistence provider.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    /**
     * The account to which this recurrence belongs.
     * <p>
     * Cannot be {@code null}.
     * </p>
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AbstractAccount account;

    /**
     * The date when the recurrence starts.
     * <p>
     * Cannot be {@code null}.
     * </p>
     */
    @NotNull
    @Column(name = "start_date", nullable = false)
    private OffsetDateTime startDate;

    /**
     * The date when the recurrence ends, if any.
     * <p>
     * May be {@code null} to indicate an indefinite recurrence.
     * </p>
     */
    @Column(name = "end_date")
    private OffsetDateTime endDate;

    /**
     * The frequency at which the recurrence repeats.
     * <p>
     * Cannot be {@code null}.
     * </p>
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 16)
    private RecurrenceFrequency frequency;

    /**
     * Protected no-args constructor for JPA.
     */
    public NormalRecurrence() {
    }

    /**
     * Creates a new recurrence with the specified details.
     *
     * @param account    the account associated with this recurrence; must not be {@code null}.
     * @param startDate  the start date of the recurrence; must not be {@code null}.
     * @param endDate    the optional end date of the recurrence; may be {@code null}.
     * @param frequency  the recurrence frequency; must not be {@code null}.
     * @throws NullPointerException if any non-nullable argument is {@code null}.
     */
    public NormalRecurrence(AbstractAccount account,
                            OffsetDateTime startDate,
                            OffsetDateTime endDate,
                            RecurrenceFrequency frequency) {
        this.account = Objects.requireNonNull(account);
        this.startDate = Objects.requireNonNull(startDate);
        this.endDate = endDate;
        this.frequency = Objects.requireNonNull(frequency);
    }

    /** {@inheritDoc} */
    @Override
    public UUID getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /** {@inheritDoc} */
    @Override
    public OffsetDateTime getStartDate() {
        return startDate;
    }

    /** {@inheritDoc} */
    @Override
    public OffsetDateTime getEndDate() {
        return endDate;
    }

    /** {@inheritDoc} */
    @Override
    public RecurrenceFrequency getFrequency() {
        return frequency;
    }

    /** {@inheritDoc} */
    @Override
    public AbstractAccount getAccount() {
        return account;
    }
}
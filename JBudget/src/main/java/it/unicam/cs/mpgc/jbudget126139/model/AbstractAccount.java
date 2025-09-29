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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract base class representing a user-owned financial account.
 * <p>
 * Provides persistence mappings, validation constraints, and common fields
 * for all account types in the application.
 * </p>
 *
 * <p>
 * Accounts are persisted using JPA single-table inheritance
 * ({@link InheritanceType#SINGLE_TABLE}), with the discriminator column
 * {@code account_type} to distinguish between account subclasses.
 * </p>
 */
@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
@Access(AccessType.FIELD)
public abstract class AbstractAccount implements Account {

    /**
     * Unique identifier of the account, generated automatically using {@link GenerationType#UUID}.
     * <p>
     * Immutable after creation.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Timestamp indicating when the account was created.
     * Automatically set by the persistence provider.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Human-readable name of the account.
     * <p>
     * Must contain between 1 and 255 characters.
     * </p>
     */
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * ISO 4217 currency code representing the account's currency.
     * <p>
     * Cannot be {@code null}.
     * </p>
     */
    @NotNull
    @Column(name = "currency", length = 3, nullable = false)
    private Currency currency;

    /**
     * Optional description of the account.
     * <p>
     * Can be {@code null} and must have a maximum length of 512 characters if present.
     * </p>
     */
    @Size(max = 512)
    @Column(name = "description", length = 512)
    private String description;

    /**
     * Protected no-args constructor for JPA.
     */
    protected AbstractAccount() {
    }

    /**
     * Constructs a new account with the specified details.
     *
     * @param name        the account name; must not be {@code null}.
     * @param currency    the account currency; must not be {@code null}.
     * @param description the optional description; may be {@code null}.
     * @throws NullPointerException if any non-nullable argument is {@code null}.
     */
    protected AbstractAccount(String name, Currency currency, String description) {
        this.name = Objects.requireNonNull(name);
        this.currency = Objects.requireNonNull(currency);
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Returns the creation timestamp of the account.
     *
     * @return the creation date and time of the account.
     */
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the name of the account.
     *
     * @return the account name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the currency of the account.
     *
     * @return the account's {@link Currency}.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Returns the description of the account.
     *
     * @return the account description, or {@code null} if none is set.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the transactions associated with this account.
     *
     * @return a set of {@link Transaction} objects linked to this account.
     */
    public abstract Set<? extends Transaction> getTransactions();

    /**
     * Updates the account details.
     *
     * @param name        the new account name; must not be {@code null}.
     * @param currency    the new currency; must not be {@code null}.
     * @param description the new description; may be {@code null}.
     * @throws NullPointerException if any non-nullable argument is {@code null}.
     */
    public void updateDetails(String name, Currency currency, String description) {
        this.name = Objects.requireNonNull(name);
        this.currency = Objects.requireNonNull(currency);
        this.description = description;
    }
}
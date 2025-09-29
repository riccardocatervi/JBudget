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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * Base class for all {@link Transaction} implementations, providing common persistence mappings
 * and validation constraints for transaction entities.
 * <p>
 * This abstract class handles transaction identity, creation timestamp, value date, amount,
 * transaction direction, optional description, linked account, and associated tag identifiers.
 * </p>
 *
 * <p>
 * Mapped as a JPA {@link MappedSuperclass}, so its fields are inherited by subclasses
 * but it is not itself a database table.
 * </p>
 */
@MappedSuperclass
@Access(AccessType.FIELD)
public abstract sealed class AbstractTransaction implements Transaction permits NormalTransaction {

    /**
     * Unique identifier of the transaction, generated automatically using {@link GenerationType#UUID}.
     * <p>
     * This value is immutable after creation.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Timestamp indicating when the transaction was created.
     * Automatically set by the persistence provider.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    /**
     * The date when the transaction takes effect (value date).
     * <p>
     * Cannot be {@code null}.
     * </p>
     */
    @NotNull
    @Column(name = "value_date", nullable = false)
    private OffsetDateTime valueDate;

    /**
     * The monetary amount of the transaction.
     * <p>
     * Must be positive, with up to 17 integer digits and 2 fractional digits.
     * Automatically normalized to two decimal places before persistence or update.
     * </p>
     */
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    @Digits(integer = 17, fraction = 2)
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Indicates whether this transaction is an income or an expense.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 16)
    private TransactionDirection direction;

    /**
     * Optional description of the transaction.
     * <p>
     * Can be {@code null} and must have a maximum length of 512 characters if present.
     * </p>
     * Automatically trimmed and set to {@code null} if empty before persistence or update.
     */
    @Size(max = 512)
    @Column(name = "description", length = 512)
    private String description;

    /**
     * The account associated with this transaction.
     * <p>
     * Cannot be {@code null}.
     * </p>
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AbstractAccount account;

    /**
     * Identifiers of the tags associated with this transaction.
     * <p>
     * Cannot contain {@code null} elements and cannot be {@code null} as a set.
     * Stored in a separate join table.
     * </p>
     */
    @NotNull
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "transaction_tags",
            joinColumns = @JoinColumn(name = "transaction_id"))
    @Column(name = "tag_id", nullable = false)
    private Set<@NotNull UUID> tags = new HashSet<>();

    /**
     * Protected no-args constructor for JPA.
     */
    protected AbstractTransaction() {
    }

    /**
     * Creates a new transaction with the provided details.
     *
     * @param valueDate   the transaction value date; must not be {@code null}.
     * @param amount      the transaction amount; must not be {@code null}.
     * @param direction   the transaction direction; must not be {@code null}.
     * @param account     the associated account; must not be {@code null}.
     * @param description the optional description; may be {@code null}.
     * @param tags        the set of associated tag IDs; may be {@code null}, in which case an empty set is used.
     * @throws NullPointerException if any non-nullable argument is {@code null}.
     */
    public AbstractTransaction(OffsetDateTime valueDate,
                               BigDecimal amount,
                               TransactionDirection direction,
                               AbstractAccount account,
                               String description,
                               Set<UUID> tags) {
        this.valueDate = Objects.requireNonNull(valueDate);
        this.amount = Objects.requireNonNull(amount);
        this.direction = Objects.requireNonNull(direction);
        this.account = Objects.requireNonNull(account);
        this.description = description;
        this.tags = (tags == null) ? new HashSet<>() : new HashSet<>(tags);
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
    public OffsetDateTime getValueDate() {
        return valueDate;
    }

    /** {@inheritDoc} */
    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    /** {@inheritDoc} */
    @Override
    public TransactionDirection getDirection() {
        return direction;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public AbstractAccount getAccount() {
        return account;
    }

    /** {@inheritDoc} */
    @Override
    public Set<UUID> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Updates the transaction details.
     *
     * @param valueDate   the new value date; must not be {@code null}.
     * @param amount      the new amount; must not be {@code null}.
     * @param direction   the new direction; must not be {@code null}.
     * @param description the new description; may be {@code null}.
     * @param tags        the new set of tag IDs; may be {@code null}, in which case an empty set is used.
     * @throws NullPointerException if any non-nullable argument is {@code null}.
     */
    public void updateDetails(OffsetDateTime valueDate,
                              BigDecimal amount,
                              TransactionDirection direction,
                              String description,
                              Set<UUID> tags) {
        this.valueDate = Objects.requireNonNull(valueDate);
        this.amount = Objects.requireNonNull(amount);
        this.direction = Objects.requireNonNull(direction);
        this.description = description;
        this.tags = (tags == null) ? new HashSet<>() : new HashSet<>(tags);
    }

    /**
     * Normalizes transaction fields before persistence or update.
     * <ul>
     *     <li>Rounds {@link #amount} to two decimal places using {@link RoundingMode#HALF_UP}.</li>
     *     <li>Trims {@link #description} and sets it to {@code null} if empty.</li>
     *     <li>Ensures {@link #tags} is non-null.</li>
     * </ul>
     */
    @PrePersist
    @PreUpdate
    private void normalize() {
        if (amount != null)
            amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (description != null) {
            description = description.trim();
            if (description.isEmpty())
                description = null;
        }
        if (tags == null) tags = new HashSet<>();
    }
}

/*
VALIDATION:
1) Creo l'oggetto -> qui agiscono le precondizioni (costruttore, Objects.requireNonNull, ecc.)
2) entityManager.persist(...)
3) @PrePersist (callback JPA) -> posso impostare default/derivati (vengono chiamati in automatico da Hibernate
    quando chiamo persist().
4) Bean Validation
5) SQL INSERT -> intervengono i vincoli DB (nullable = false, FK, unique, ...)
 */
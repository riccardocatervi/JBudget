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

import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

/**
 * Concrete implementation of an {@link AbstractAccount} representing a standard,
 * user-managed account without special behaviors.
 * <p>
 * Uses the discriminator value {@code NORMAL} in the single-table inheritance strategy.
 * </p>
 *
 * <p>
 * Stores its associated {@link NormalTransaction} entities in a bidirectional
 * one-to-many relationship.
 * </p>
 */
@Entity
@DiscriminatorValue("NORMAL")
public class NormalAccount extends AbstractAccount {

    /**
     * Transactions associated with this account.
     * <p>
     * Mapped as a bidirectional {@link OneToMany} relationship to {@link NormalTransaction},
     * with cascade operations enabled and orphan removal active.
     * </p>
     * Fetched lazily by default.
     */
    @OneToMany(mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<NormalTransaction> transactions = new HashSet<>();

    /**
     * Protected no-args constructor for JPA.
     */
    public NormalAccount() {
        super();
    }

    /**
     * Constructs a new normal account with the given details.
     *
     * @param name        the account name; must not be {@code null}.
     * @param currency    the account currency; must not be {@code null}.
     * @param description the optional description; may be {@code null}.
     */
    public NormalAccount(String name, Currency currency, String description) {
        super(name, currency, description);
        this.transactions = new HashSet<>();
    }

    /**
     * Returns an unmodifiable view of the transactions associated with this account.
     *
     * @return an unmodifiable {@link Set} of {@link Transaction} objects.
     */
    @Override
    public Set<? extends Transaction> getTransactions() {
        return Collections.unmodifiableSet(transactions);
    }
}
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

package it.unicam.cs.mpgc.jbudget126139.persistence;

import it.unicam.cs.mpgc.jbudget126139.model.*;
import it.unicam.cs.mpgc.jbudget126139.persistence.impl.AccountRepositoryImpl;
import it.unicam.cs.mpgc.jbudget126139.persistence.impl.RecurrenceRepositoryImpl;
import it.unicam.cs.mpgc.jbudget126139.persistence.impl.TagRepositoryImpl;
import it.unicam.cs.mpgc.jbudget126139.persistence.impl.TransactionRepositoryImpl;
import jakarta.persistence.EntityManager;

/**
 * Factory class for creating repository instances for specific entity types.
 * <p>
 * Each factory method returns a type-safe repository for the corresponding
 * entity, configured with the given {@link EntityManager}.
 * </p>
 */
public class RepositoryFactory {

    /**
     * Creates an {@link AccountRepository} for managing {@link NormalAccount} entities.
     *
     * @param em the {@link EntityManager} to use; must not be {@code null}.
     * @return a new {@link AccountRepository} instance for {@link NormalAccount}.
     */
    public static AccountRepository<NormalAccount> createAccountRepository(EntityManager em) {
        return new AccountRepositoryImpl<>(em, NormalAccount.class);
    }

    /**
     * Creates a {@link TransactionRepository} for managing {@link NormalTransaction} entities.
     *
     * @param em the {@link EntityManager} to use; must not be {@code null}.
     * @return a new {@link TransactionRepository} instance for {@link NormalTransaction}.
     */
    public static TransactionRepository<NormalTransaction> createTransactionRepository(EntityManager em) {
        return new TransactionRepositoryImpl<>(em, NormalTransaction.class);
    }

    /**
     * Creates a {@link TagRepository} for managing {@link NormalTag} entities.
     *
     * @param em the {@link EntityManager} to use; must not be {@code null}.
     * @return a new {@link TagRepository} instance for {@link NormalTag}.
     */
    public static TagRepository<NormalTag> createTagRepository(EntityManager em) {
        return new TagRepositoryImpl<>(em, NormalTag.class);
    }

    /**
     * Creates a {@link RecurrenceRepository} for managing {@link NormalRecurrence} entities.
     *
     * @param em the {@link EntityManager} to use; must not be {@code null}.
     * @return a new {@link RecurrenceRepository} instance for {@link NormalRecurrence}.
     */
    public static RecurrenceRepository<NormalRecurrence> createRecurrenceRepository(EntityManager em) {
        return new RecurrenceRepositoryImpl<>(em, NormalRecurrence.class);
    }
}
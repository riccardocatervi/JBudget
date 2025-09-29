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

package it.unicam.cs.mpgc.jbudget126139.service;

import it.unicam.cs.mpgc.jbudget126139.persistence.RepositoryFactory;
import it.unicam.cs.mpgc.jbudget126139.service.impl.*;
import jakarta.persistence.EntityManagerFactory;


/**
 * Factory class for creating service layer instances.
 * <p>
 * This class centralizes the construction of service implementations,
 * wiring them with their respective repositories and dependencies.
 * </p>
 */
public class ServiceFactory {

    /**
     * Creates an {@link AccountService} instance.
     *
     * @param emf                 the {@link EntityManagerFactory}; must not be {@code null}
     * @return a new {@link AccountService} instance
     */
    public static AccountService createAccountService(EntityManagerFactory emf) {
        return new AccountServiceImpl(emf, RepositoryFactory::createAccountRepository);
    }

    /**
     * Creates a {@link TransactionService} instance.
     *
     * @param emf the {@link EntityManagerFactory}; must not be {@code null}
     * @return a new {@link TransactionService} instance
     */
    public static TransactionService createTransactionService(EntityManagerFactory emf) {
        return new TransactionServiceImpl(emf, RepositoryFactory::createTransactionRepository);
    }

    /**
     * Creates a {@link TagService} instance.
     *
     * @param emf the {@link EntityManagerFactory}; must not be {@code null}
     * @return a new {@link TagService} instance
     */
    public static TagService createTagService(EntityManagerFactory emf) {
        return new TagServiceImpl(emf, RepositoryFactory::createTagRepository);
    }

    /**
     * Creates a {@link RecurrenceService} instance.
     *
     * @param emf the {@link EntityManagerFactory}; must not be {@code null}
     * @return a new {@link RecurrenceService} instance
     */
    public static RecurrenceService createRecurrenceService(EntityManagerFactory emf) {
        return new RecurrenceServiceImpl(emf, RepositoryFactory::createRecurrenceRepository);
    }

    /**
     * Creates a {@link RecurrenceTransactionService} instance.
     *
     * @param emf the {@link EntityManagerFactory}; must not be {@code null}
     * @return a new {@link RecurrenceTransactionService} instance
     */
    public static RecurrenceTransactionService createRecurrenceTransactionService(EntityManagerFactory emf) {
        return new RecurrenceTransactionServiceImpl(
                createTransactionService(emf),
                createRecurrenceService(emf)
        );
    }
}
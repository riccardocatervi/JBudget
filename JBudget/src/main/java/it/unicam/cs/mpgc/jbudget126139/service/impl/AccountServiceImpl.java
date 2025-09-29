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

package it.unicam.cs.mpgc.jbudget126139.service.impl;

import it.unicam.cs.mpgc.jbudget126139.model.NormalAccount;
import it.unicam.cs.mpgc.jbudget126139.persistence.AccountRepository;
import it.unicam.cs.mpgc.jbudget126139.service.AccountService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;
import it.unicam.cs.mpgc.jbudget126139.service.mapper.AccountMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;

import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/**
 * Default implementation of {@link AccountService}.
 * <p>
 * Handles account creation, retrieval, update, deletion, and listing.
 * </p>
 */
public class AccountServiceImpl implements AccountService {

    private final EntityManagerFactory emf;
    private final Function<EntityManager, AccountRepository<NormalAccount>> accountRepositoryProvider;

    /**
     * Creates a new {@code AccountServiceImpl} instance.
     *
     * @param emf                       the {@link EntityManagerFactory}; must not be {@code null}
     * @param accountRepositoryProvider a function to create an {@link AccountRepository}; must not be {@code null}
     */
    public AccountServiceImpl(EntityManagerFactory emf,
                              Function<EntityManager, AccountRepository<NormalAccount>> accountRepositoryProvider) {
        // RIMUOVERE: Supplier<User> currentUserProvider
        this.emf = Objects.requireNonNull(emf, "EntityManagerFactory must not be null");
        this.accountRepositoryProvider = Objects.requireNonNull(accountRepositoryProvider,
                "AbstractAccount repository provider must not be null");
    }

    /** {@inheritDoc} */
    @Override
    public AccountDTO createAccount(String name, Currency currency, String description) {
        requireNonNulls(name, currency);
        validateName(name);
        return executeInTransaction(em -> {
            var repo = accountRepositoryProvider.apply(em);
            NormalAccount account = new NormalAccount(name, currency, description);
            repo.save(account);
            return AccountMapper.INSTANCE.toDto(account);
        });
    }

    /** {@inheritDoc} */
    @Override
    public AccountDTO getAccount(UUID accountId) {
        requireNonNulls(accountId);
        return executeReadOnly(em -> {
            var repo = accountRepositoryProvider.apply(em);
            return repo.findById(accountId)
                    .map(AccountMapper.INSTANCE::toDto)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("AbstractAccount with id %s not found", accountId)));
        });
    }

    /** {@inheritDoc} */
    @Override
    public AccountDTO updateAccount(UUID accountId, String name, Currency currency, String description) {
        requireNonNulls(accountId, name, currency);
        validateName(name);
        return executeInTransaction(em -> {
            var repo = accountRepositoryProvider.apply(em);
            var account = repo.findById(accountId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("AbstractAccount with id %s not found", accountId)));
            account.updateDetails(name, currency, description);
            return AccountMapper.INSTANCE.toDto(account);
        });
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAccount(UUID accountId) {
        requireNonNulls(accountId);
        executeInTransaction(em -> {
            var repo = accountRepositoryProvider.apply(em);
            var account = repo.findById(accountId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            String.format("AbstractAccount with id %s not found", accountId)));
            if (!account.getTransactions().isEmpty())
                throw new IllegalStateException(
                        String.format("Cannot delete account %s: it has %d transactions",
                                accountId, account.getTransactions().size()));
            repo.delete(account);
            return null;
        });
    }

    /** {@inheritDoc} */
    @Override
    public List<AccountDTO> listAccounts() {
        return executeReadOnly(em -> {
            var repo = accountRepositoryProvider.apply(em);
            // MODIFICARE: List<NormalAccount> accounts = repo.findByUserOrderByNameAsc(currentUserProvider.get().getId());
            List<NormalAccount> accounts = repo.findAllOrderByNameAsc();
            return mapToAccountDTOs(accounts);
        });
    }


    // Private helper methods

    private <T> T executeInTransaction(Function<EntityManager, T> operation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            T result = operation.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException e) {
            rollbackSafely(em);
            throw e;
        } finally {
            em.close();
        }
    }

    private <T> T executeReadOnly(Function<EntityManager, T> operation) {
        EntityManager em = emf.createEntityManager();
        try {
            return operation.apply(em);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            em.close();
        }
    }

    private void rollbackSafely(EntityManager em) {
        try {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
        } catch (RuntimeException rollbackEx) {
        }
    }

    private List<AccountDTO> mapToAccountDTOs(List<NormalAccount> accounts) {
        return accounts.stream()
                .map(AccountMapper.INSTANCE::toDto)
                .toList();
    }

    private void validateName(String name) {
        if (name.trim().isEmpty())
            throw new IllegalArgumentException("AbstractAccount name must not be empty");
    }

    private void requireNonNulls(Object... objects) {
        for (Object obj : objects)
            Objects.requireNonNull(obj, "Parameter must not be null");
    }
}
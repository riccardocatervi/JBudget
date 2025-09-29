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

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing accounts.
 * <p>
 * Provides methods for creating, retrieving, updating, deleting,
 * and listing accounts.
 * </p>
 */
public interface AccountService {

    /**
     * Creates a new account.
     *
     * @param name        the name of the account; must not be {@code null} or blank
     * @param currency    the currency of the account; must not be {@code null}
     * @param description an optional description of the account; may be {@code null}
     * @return the created {@link AccountDTO}
     */
    AccountDTO createAccount(String name, Currency currency, String description);

    /**
     * Retrieves an account by its unique identifier.
     *
     * @param accountId the account identifier; must not be {@code null}
     * @return the matching {@link AccountDTO}
     * @throws java.util.NoSuchElementException if no account with the given ID exists
     */
    AccountDTO getAccount(UUID accountId);

    /**
     * Updates an existing account.
     *
     * @param accountId   the account identifier; must not be {@code null}
     * @param name        the new name of the account; must not be {@code null} or blank
     * @param currency    the new currency of the account; must not be {@code null}
     * @param description the new description of the account; may be {@code null}
     * @return the updated {@link AccountDTO}
     */
    AccountDTO updateAccount(UUID accountId, String name, Currency currency, String description);

    /**
     * Deletes an account by its unique identifier.
     *
     * @param accountId the account identifier; must not be {@code null}
     */
    void deleteAccount(UUID accountId);

    /**
     * Retrieves all accounts, typically ordered by name.
     *
     * @return a list of {@link AccountDTO} objects; never {@code null}, may be empty
     */
    List<AccountDTO> listAccounts();
}
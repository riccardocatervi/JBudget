package it.unicam.cs.mpgc.jbudget126139.controller;

import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;

import java.util.List;
import java.util.UUID;

/**
 * Controller interface for managing accounts.
 * <p>
 * Defines the operations for creating, retrieving, updating,
 * deleting, and listing accounts in the application.
 * </p>
 */
public interface AccountController {

    /**
     * Creates a new account.
     *
     * @param name         the account name (must not be {@code null} or empty)
     * @param currencyCode the ISO currency code (e.g., "USD", "EUR")
     * @param description  an optional description, may be {@code null}
     * @return the created {@link AccountDTO}
     */
    AccountDTO createAccount(String name, String currencyCode, String description);

    /**
     * Retrieves an account by its unique identifier.
     *
     * @param accountId the ID of the account to retrieve
     * @return the {@link AccountDTO}, or {@code null} if not found
     */
    AccountDTO getAccount(UUID accountId);

    /**
     * Updates an existing account.
     *
     * @param accountId    the ID of the account to update
     * @param name         the new name of the account
     * @param currencyCode the new ISO currency code
     * @param description  the new description, may be {@code null}
     * @return the updated {@link AccountDTO}
     */
    AccountDTO updateAccount(UUID accountId, String name, String currencyCode, String description);

    /**
     * Deletes an account by its unique identifier.
     *
     * @param accountId the ID of the account to delete
     */
    void deleteAccount(UUID accountId);

    /**
     * Lists all accounts.
     *
     * @return a list of all {@link AccountDTO} objects
     */
    List<AccountDTO> listAccounts();
}
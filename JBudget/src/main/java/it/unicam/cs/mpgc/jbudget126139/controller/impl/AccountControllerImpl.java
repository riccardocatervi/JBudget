package it.unicam.cs.mpgc.jbudget126139.controller.impl;

import it.unicam.cs.mpgc.jbudget126139.controller.AccountController;
import it.unicam.cs.mpgc.jbudget126139.service.AccountService;
import it.unicam.cs.mpgc.jbudget126139.service.dto.AccountDTO;

import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of {@link AccountController} that delegates
 * account management operations to an {@link AccountService}.
 * <p>
 * Validates input parameters and handles currency code conversion.
 * </p>
 */
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;

    /**
     * Creates a new {@code AccountControllerImpl}.
     *
     * @param accountService the service handling account operations (must not be null)
     */
    public AccountControllerImpl(AccountService accountService) {
        this.accountService = Objects.requireNonNull(accountService);
    }

    /**
     * Creates a new account with the given details.
     *
     * @param name         the account name
     * @param currencyCode the ISO 4217 currency code
     * @param description  the account description (optional)
     * @return the created {@link AccountDTO}
     * @throws IllegalArgumentException if the currency code is invalid
     * @throws NullPointerException     if {@code name} or {@code currencyCode} is null
     */
    @Override
    public AccountDTO createAccount(String name, String currencyCode, String description) {
        Currency currency = parseCurrency(currencyCode);
        return accountService.createAccount(name, currency, description);
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the account ID
     * @return the {@link AccountDTO} if found
     * @throws NullPointerException if {@code accountId} is null
     */
    @Override
    public AccountDTO getAccount(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        return accountService.getAccount(accountId);
    }

    /**
     * Updates an existing account with new details.
     *
     * @param accountId    the account ID
     * @param name         the new account name
     * @param currencyCode the ISO 4217 currency code
     * @param description  the new description (optional)
     * @return the updated {@link AccountDTO}
     * @throws IllegalArgumentException if the currency code is invalid
     * @throws NullPointerException     if {@code accountId} or {@code currencyCode} is null
     */
    @Override
    public AccountDTO updateAccount(UUID accountId, String name, String currencyCode, String description) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        Currency currency = parseCurrency(currencyCode);
        return accountService.updateAccount(accountId, name, currency, description);
    }

    /**
     * Deletes an account by its ID.
     *
     * @param accountId the account ID
     * @throws NullPointerException if {@code accountId} is null
     */
    @Override
    public void deleteAccount(UUID accountId) {
        Objects.requireNonNull(accountId, "accountId must not be null");
        accountService.deleteAccount(accountId);
    }

    /**
     * Retrieves all accounts.
     *
     * @return the list of {@link AccountDTO}
     */
    @Override
    public List<AccountDTO> listAccounts() {
        return accountService.listAccounts();
    }

    private Currency parseCurrency(String code) {
        Objects.requireNonNull(code, "currencyCode must not be null");
        String trimmed = code.trim().toUpperCase();
        try {
            return Currency.getInstance(trimmed);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid currency code: " + trimmed);
        }
    }
}
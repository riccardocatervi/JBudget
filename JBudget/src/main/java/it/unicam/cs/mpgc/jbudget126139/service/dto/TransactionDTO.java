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

package it.unicam.cs.mpgc.jbudget126139.service.dto;

import it.unicam.cs.mpgc.jbudget126139.model.TransactionDirection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a financial transaction.
 *
 * @param id           the unique identifier of the transaction
 * @param accountId    the identifier of the account associated with the transaction
 * @param valueDate    the date when the transaction is effective
 * @param amount       the monetary amount of the transaction
 * @param direction    the transaction direction (credit or debit)
 * @param description  an optional description of the transaction; may be {@code null}
 * @param tagIds       the identifiers of tags associated with the transaction; never {@code null}
 * @param recurrenceId the identifier of the associated recurrence; may be {@code null}
 */
public record TransactionDTO(
        UUID id,
        UUID accountId,
        OffsetDateTime valueDate,
        BigDecimal amount,
        TransactionDirection direction,
        String description,
        Set<UUID> tagIds,
        UUID recurrenceId
) {
    /**
     * Compact constructor that ensures {@code tagIds} is never {@code null}
     * and that its content is immutable.
     *
     * @param id           the unique identifier of the transaction
     * @param accountId    the identifier of the account
     * @param valueDate    the date when the transaction is effective
     * @param amount       the monetary amount
     * @param direction    the transaction direction
     * @param description  the description of the transaction
     * @param tagIds       the identifiers of associated tags
     * @param recurrenceId the identifier of the recurrence
     */
    public TransactionDTO {
        tagIds = (tagIds == null) ? Set.of() : Set.copyOf(tagIds);
    }
}
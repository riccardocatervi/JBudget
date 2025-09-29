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

import it.unicam.cs.mpgc.jbudget126139.model.RecurrenceFrequency;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a recurrence.
 *
 * @param id         the unique identifier of the recurrence
 * @param createdAt  the timestamp when the recurrence was created
 * @param accountId  the identifier of the account associated with the recurrence
 * @param startDate  the start date of the recurrence
 * @param endDate    the optional end date of the recurrence; may be {@code null}
 * @param frequency  the recurrence frequency (e.g., daily, weekly, monthly, yearly)
 */
public record RecurrenceDTO(
        UUID id,
        OffsetDateTime createdAt,
        UUID accountId,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        RecurrenceFrequency frequency
) {
}
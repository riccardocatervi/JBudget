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

package it.unicam.cs.mpgc.jbudget126139.ui.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

/**
 * Utility class providing static helper methods for formatting values such as
 * currency, dates, percentages, large numbers, and text.
 * <p>
 * This class centralizes presentation logic used across the UI layer to
 * maintain consistent formatting.
 * </p>
 */
public class FormatUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private static final DateTimeFormatter SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd");

    /**
     * Formats a numeric amount into a currency string with the appropriate symbol.
     *
     * @param amount   the amount to format
     * @param currency the currency used for formatting
     * @return a formatted currency string (e.g., "$1,234.56")
     */
    public static String formatCurrency(BigDecimal amount, Currency currency) {
        if (amount == null) return "0.00";

        String symbol = getCurrencySymbol(currency.getCurrencyCode());
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return symbol + formatter.format(amount);
    }

    /**
     * Formats a currency amount with a leading plus or minus sign.
     *
     * @param amount   the amount to format
     * @param currency the currency used for formatting
     * @param isIncome {@code true} if the amount is income (positive), {@code false} if expense
     * @return a signed currency string (e.g., "+$200.00" or "-$50.00")
     */
    public static String formatCurrencyWithSign(BigDecimal amount, Currency currency, boolean isIncome) {
        String formatted = formatCurrency(amount, currency);
        return (isIncome ? "+" : "-") + formatted;
    }

    /**
     * Returns the display symbol for a given currency code.
     *
     * @param currencyCode the ISO currency code (e.g., "USD", "EUR")
     * @return the currency symbol (e.g., "$", "€")
     */
    public static String getCurrencySymbol(String currencyCode) {
        return switch (currencyCode) {
            case "USD", "CAD", "AUD" -> "$";
            case "EUR" -> "€";
            case "GBP" -> "£";
            case "JPY" -> "¥";
            case "CHF" -> "CHF ";
            case "KES" -> "KSh ";
            default -> "$";
        };
    }

    /**
     * Formats a date as a human-readable string (no time).
     *
     * @param dateTime the date to format
     * @return formatted date (e.g., "Jan 12, 2025"), or an empty string if null
     */
    public static String formatDate(OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Formats a date and time into a human-readable string.
     *
     * @param dateTime the date and time to format
     * @return formatted string (e.g., "Jan 12, 2025 14:30"), or an empty string if null
     */
    public static String formatDateTime(OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(TIME_FORMATTER);
    }

    /**
     * Formats a date into a short representation (month and day only).
     *
     * @param dateTime the date to format
     * @return short date string (e.g., "Jan 12"), or an empty string if null
     */
    public static String formatShortDate(OffsetDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(SHORT_DATE_FORMATTER);
    }

    /**
     * Formats a value as a percentage string.
     *
     * @param value the value to convert (e.g., 0.25 for 25%)
     * @return formatted percentage string (e.g., "25.0%")
     */
    public static String formatPercentage(double value) {
        return String.format("%.1f%%", value * 100);
    }

    /**
     * Formats large numbers into human-readable abbreviations.
     *
     * @param number the number to format
     * @return string with suffix (e.g., "1.5K", "2.3M")
     */
    public static String formatLargeNumber(BigDecimal number) {
        if (number == null) return "0";

        double value = number.doubleValue();
        if (value >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.1fK", value / 1_000);
        } else {
            return String.format("%.0f", value);
        }
    }

    /**
     * Truncates text to a maximum length, appending ellipsis ("...") if necessary.
     *
     * @param text      the text to truncate
     * @param maxLength maximum allowed length
     * @return truncated text, or the original text if shorter than maxLength
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Returns a relative time description compared to the current moment.
     *
     * @param dateTime the date and time to evaluate
     * @return relative time string (e.g., "Today", "3 days ago", "2 months ago")
     */
    public static String getRelativeTime(OffsetDateTime dateTime) {
        if (dateTime == null) return "";

        OffsetDateTime now = OffsetDateTime.now();
        long days = java.time.Duration.between(dateTime, now).toDays();

        if (days == 0) {
            return "Today";
        } else if (days == 1) {
            return "Yesterday";
        } else if (days < 7) {
            return days + " days ago";
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks + (weeks == 1 ? " week ago" : " weeks ago");
        } else if (days < 365) {
            long months = days / 30;
            return months + (months == 1 ? " month ago" : " months ago");
        } else {
            long years = days / 365;
            return years + (years == 1 ? " year ago" : " years ago");
        }
    }
}
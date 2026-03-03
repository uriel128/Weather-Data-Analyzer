package com.example.weatherdataanalyzer;

import java.util.Optional;

/**
 * # Input Parser
 *
 * Functional parsing helpers for UI and CSV values.
 */
public interface InputParser {

    /**
     * Parses a value into a `double` using pattern matching in `switch`.
     *
     * @param value any numeric source value
     * @return parsed double
     */
    static double toDouble(Object value) {
        return switch (value) {
            case Number number -> number.doubleValue();
            case String text when !text.isBlank() -> Double.parseDouble(text.trim());
            default -> throw new IllegalArgumentException("Value cannot be converted to double: " + value);
        };
    }

    /**
     * Parses day-of-month in range `1..31`.
     *
     * @param input day text
     * @return parsed day or empty when invalid
     */
    static Optional<Integer> parseDay(String input) {
        try {
            var value = Integer.parseInt(input == null ? "" : input.trim());
            return value >= 1 && value <= 31 ? Optional.of(value) : Optional.empty();
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Parses temperature threshold.
     *
     * @param input threshold text
     * @return parsed threshold or empty when invalid
     */
    static Optional<Double> parseThreshold(String input) {
        try {
            return Optional.of(toDouble(input));
        } catch (RuntimeException ignored) {
            return Optional.empty();
        }
    }
}

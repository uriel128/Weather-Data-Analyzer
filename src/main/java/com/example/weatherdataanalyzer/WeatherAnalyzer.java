package com.example.weatherdataanalyzer;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * # Weather Analyzer
 *
 * Performs weather calculations with lambdas and streams.
 *
 * @param records full dataset
 */
record WeatherAnalyzer(List<WeatherRecord> records) {

    /**
     * Returns all records for a year/month.
     *
     * @param year selected year
     * @param month selected month
     * @return ordered weather rows for the month
     */
    List<WeatherRecord> recordsForMonth(int year, Month month) {
        Metric<List<WeatherRecord>> metric = rows -> rows.stream()
                .filter(row -> row.date().getYear() == year)
                .filter(row -> row.date().getMonth() == month)
                .sorted(Comparator.comparing(WeatherRecord::date))
                .toList();
        return metric.calculate(records);
    }

    /**
     * Dedicated top card content for one date.
     *
     * @param record selected date record
     * @return formatted details block
     */
    String dateCardDetails(WeatherRecord record) {
        return """
                Date: %s
                Temperature: %.1f F
                Category: %s
                Humidity: %.1f %%
                Precipitation: %.1f mm
                """
                .formatted(
                        record.date(),
                        record.temperatureF(),
                        record.category().label(),
                        record.humidityPercent(),
                        record.precipitationMm()
                )
                .stripIndent();
    }

    /**
     * Summary content for the lower panel.
     *
     * @param year selected year
     * @param month selected month
     * @param thresholdF active threshold in Fahrenheit
     * @return formatted monthly summary
     */
    String monthPanelSummary(int year, Month month, double thresholdF) {
        var monthRows = recordsForMonth(year, month);
        var avg = monthRows.stream().mapToDouble(WeatherRecord::temperatureF).average().orElse(Double.NaN);
        var rainy = monthRows.stream().filter(WeatherRecord::isRainy).count();

        return """
                Month: %s %d
                Average temperature: %s F
                Rainy days: %,d
                Active threshold: %.1f F
                """
                .formatted(
                        month.getDisplayName(TextStyle.FULL, Locale.US),
                        year,
                        Double.isNaN(avg) ? "N/A" : "%.2f".formatted(avg),
                        rainy,
                        thresholdF
                )
                .stripIndent();
    }

    /**
     * Full list of month days with highlight and threshold state.
     *
     * @param year selected year
     * @param month selected month
     * @param thresholdC active threshold in Celsius
     * @param chosenDate selected date to highlight
     * @return rows for month list view
     */
    List<DayLine> monthLines(int year, Month month, double thresholdC, LocalDate chosenDate) {
        return recordsForMonth(year, month).stream()
                .map(row -> new DayLine(
                        row,
                        row.date().equals(chosenDate),
                        row.temperatureC() >= thresholdC
                ))
                .toList();
    }
}

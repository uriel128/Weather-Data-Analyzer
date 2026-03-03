package com.example.weatherdataanalyzer;

/**
 * Row model for month list rendering.
 *
 * @param record underlying weather row
 * @param selected whether the row is the chosen date
 * @param matchesThreshold whether the row meets the threshold filter
 */
record DayLine(WeatherRecord record, boolean selected, boolean matchesThreshold) {

    /**
     * Render line for list UI.
     *
     * @return formatted label text
     */
    String renderLabel() {
        var prefix = selected ? "* " : "  ";
        return "%s%s | %.1f F | %s".formatted(prefix, record.date(), record.temperatureF(), record.category().label());
    }
}

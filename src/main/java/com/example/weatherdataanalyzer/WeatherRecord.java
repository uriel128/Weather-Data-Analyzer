package com.example.weatherdataanalyzer;

import java.time.LocalDate;

/**
 * # Weather Record
 *
 * Immutable weather row loaded from CSV.
 *
 * @param date observation date
 * @param temperatureC temperature in Celsius
 * @param humidityPercent humidity percentage
 * @param precipitationMm precipitation in millimeters
 */
record WeatherRecord(LocalDate date, double temperatureC, double humidityPercent, double precipitationMm) {

    /**
     * Rainy day predicate.
     *
     * @return `true` when precipitation is above zero
     */
    boolean isRainy() {
        return precipitationMm > 0;
    }

    /**
     * Weather category via enhanced switch using Fahrenheit bands.
     *
     * @return weather category
     */
    WeatherCategory category() {
        var f = TemperatureSupport.cToF(temperatureC);
        return switch (Integer.valueOf((int) Math.round(f))) {
            case Integer tempF when tempF >= 86 -> WeatherCategory.HOT;
            case Integer tempF when tempF >= 68 -> WeatherCategory.WARM;
            default -> WeatherCategory.COLD;
        };
    }

    /**
     * Temperature in Fahrenheit.
     *
     * @return Fahrenheit value
     */
    double temperatureF() {
        return TemperatureSupport.cToF(temperatureC);
    }
}

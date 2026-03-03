package com.example.weatherdataanalyzer;

import java.util.List;

/**
 * Functional metric contract.
 *
 * @param <R> metric return type
 */
@FunctionalInterface
interface Metric<R> {

    /**
     * Calculates a metric for a collection of weather rows.
     *
     * @param records source rows
     * @return computed metric
     */
    R calculate(List<WeatherRecord> records);
}

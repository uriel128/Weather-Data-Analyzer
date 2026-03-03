package com.example.weatherdataanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * # CSV Data Source
 *
 * Resource-based weather CSV parser.
 */
public interface WeatherCsvDataSource {

    /**
     * Parses weather records from a classpath CSV resource.
     *
     * @param resourceName CSV resource in the package resource folder
     * @return sorted immutable weather rows
     * @throws IOException when resource cannot be read
     *
     * @snippet :
     * var records = WeatherCsvDataSource.parseCsvResource("weather.csv");
     * System.out.println(records.size());
     */
    static List<WeatherRecord> parseCsvResource(String resourceName) throws IOException {
        var stream = WeatherCsvDataSource.class.getResourceAsStream(resourceName);
        if (stream == null) {
            throw new IOException("Resource not found: " + resourceName);
        }

        try (var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .map(line -> line.split(",", -1))
                    .map(WeatherCsvDataSource::parseRecord)
                    .flatMap(Optional::stream)
                    .sorted(Comparator.comparing(WeatherRecord::date))
                    .toList();
        }
    }

    /**
     * Parses one CSV row into a weather record.
     *
     * @param cells split row
     * @return parsed weather row or empty when invalid
     */
    static Optional<WeatherRecord> parseRecord(String[] cells) {
        return switch (cells) {
            case String[] row when row.length == 4 -> {
                try {
                    var date = LocalDate.parse(row[0].trim());
                    var temp = InputParser.toDouble(row[1]);
                    var humidity = InputParser.toDouble(row[2]);
                    var precipitation = InputParser.toDouble(row[3]);
                    yield Optional.of(new WeatherRecord(date, temp, humidity, precipitation));
                } catch (RuntimeException ignored) {
                    yield Optional.empty();
                }
            }
            default -> Optional.empty();
        };
    }
}

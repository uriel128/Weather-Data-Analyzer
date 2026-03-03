# Weather Data Analyzer (Java 15-23 Features + JavaFX)

A modern Java desktop application that parses weather data from CSV and analyzes:

- Average temperature for a specific month
- Days with temperatures above a threshold
- Count of rainy days
- Weather categories (`Hot`, `Warm`, `Cold`) via enhanced switch

The project is implemented without explicit user-defined classes for domain/application design. It uses Java interfaces, records, enums, lambdas, streams, and virtual threads.

## Modern Java Features Demonstrated

- **Records**: Immutable weather domain model (`WeatherRecord`) and analysis container (`WeatherAnalyzer`)
- **Enhanced switch expressions**: Season and weather category mapping
- **Pattern matching for switch**: CSV parsing and value conversion logic
- **Text blocks**: Rich analysis report output
- **`String.stripIndent()`**: Cleanup for text block formatting
- **Lambdas + Streams**: All analysis metrics and filtering
- **Virtual threads**: Background CSV loading and analysis (`Executors.newVirtualThreadPerTaskExecutor()`)
- **Markdown-oriented Javadoc + snippets**: Documentation style aligned with modern JDK docs tooling

## Tech Stack

- Java: **JDK 25** (code uses features introduced between Java 15 and Java 23)
- UI: JavaFX 21
- Build Tool: Maven

## Project Structure

- `src/main/java/com/example/weatherdataanalyzer/Launcher.java`
  - Interface entry point (`main`) only
- `src/main/java/com/example/weatherdataanalyzer/WeatherDataApp.java`
  - JavaFX UI composition and event wiring
- `src/main/java/com/example/weatherdataanalyzer/WeatherCsvDataSource.java`
  - CSV resource parsing with pattern matching switch
- `src/main/java/com/example/weatherdataanalyzer/WeatherAnalyzer.java`
  - Stream/lambda-based weather analytics
- `src/main/java/com/example/weatherdataanalyzer/WeatherRecord.java`
  - Immutable weather record model
- `src/main/java/com/example/weatherdataanalyzer/DayLine.java`
  - Month list row model
- `src/main/java/com/example/weatherdataanalyzer/Metric.java`
  - Functional interface for reusable calculations
- `src/main/java/com/example/weatherdataanalyzer/TemperatureSupport.java`
  - Fahrenheit/Celsius conversion helpers
- `src/main/java/com/example/weatherdataanalyzer/InputParser.java`
  - Numeric/day parsing helpers
- `src/main/java/com/example/weatherdataanalyzer/AppConfig.java`
  - Centralized app constants
- `src/main/java/com/example/weatherdataanalyzer/WeatherCategory.java`
  - Enhanced-switch weather categories
- `src/main/resources/com/example/weatherdataanalyzer/weather-theme.css`
  - Modern GUI styling
- `src/main/resources/com/example/weatherdataanalyzer/weather.csv`
  - Built-in dataset used by the application (2021-01-01 to 2026-12-31)

## CSV Format

Expected header:

```csv
date,temperature,humidity,precipitation
```

Sample row:

```csv
2026-03-03,30.6,43,0.0
```

## How to Run

### 1) Build

```bash
./mvnw clean compile
```

### 2) Launch App

```bash
./mvnw javafx:run
```

## GUI Workflow

1. The application auto-loads the embedded `weather.csv` dataset at startup.
2. Choose `Year`, `Month`, and a day number (e.g., `25`).
3. Click **Update View**.
4. Top card:
   - shows weather details only for the selected date.
5. Bottom section:
   - average temperature for selected month/year
   - rainy day count for selected month/year
   - all days in selected month with `Hot/Warm/Cold` (Fahrenheit-based bands)
   - selected day highlighted
   - threshold box (`F`) greys out rows below threshold

## Notes on Constraints

- No explicit `class` declarations are used in the application design code.
- Application logic relies on records, interfaces, enums, and functional programming.

## Suggested GitHub Upload Checklist

- [x] Source code in `src/`
- [x] `README.md` with full explanation and run instructions
- [x] Maven wrapper files (`mvnw`, `mvnw.cmd`, `.mvn/`)
- [x] Embedded CSV dataset (`weather.csv`)

## Example Selected-Date Output

```text
Date: 2026-01-25
Temperature: 48.2 F
Category: Cold
Humidity: 78.4 %
Precipitation: 2.1 mm
```

package com.scheduler.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Base CSV parser with common functionality.
 */
public abstract class CsvParser<T> {
    
    protected final String fileName;

    public CsvParser(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Parses the CSV file and returns a list of parsed objects.
     */
    public List<T> parse(Path filePath) throws ParseException, IOException {
        List<T> results = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            int lineNumber = 0;
            
            // Skip header line
            line = reader.readLine();
            lineNumber++;
            
            if (line == null) {
                throw new ParseException("File is empty", fileName, lineNumber);
            }
            
            // Process data lines
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    T parsed = parseLine(line, lineNumber);
                    results.add(parsed);
                } catch (Exception e) {
                    throw new ParseException(
                        "Failed to parse line: " + e.getMessage(),
                        fileName,
                        lineNumber,
                        e
                    );
                }
            }
        }
        
        return results;
    }

    /**
     * Parses a single line from the CSV file.
     */
    protected abstract T parseLine(String line, int lineNumber) throws ParseException;

    /**
     * Splits a CSV line respecting quoted values.
     */
    protected String[] splitCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString().trim());
        return fields.toArray(new String[0]);
    }

    /**
     * Validates that the required number of fields are present.
     */
    protected void validateFieldCount(String[] fields, int expected, int lineNumber) 
            throws ParseException {
        if (fields.length < expected) {
            throw new ParseException(
                String.format("Expected at least %d fields, found %d", expected, fields.length),
                fileName,
                lineNumber
            );
        }
    }

    /**
     * Parses an integer field with validation.
     */
    protected int parseIntField(String value, String fieldName, int lineNumber) 
            throws ParseException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParseException(
                String.format("Invalid integer value for %s: '%s'", fieldName, value),
                fileName,
                lineNumber,
                e
            );
        }
    }

    /**
     * Parses a semicolon-separated list of values.
     */
    protected List<String> parseSemicolonList(String value) {
        if (value == null || value.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] parts = value.split(";");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}

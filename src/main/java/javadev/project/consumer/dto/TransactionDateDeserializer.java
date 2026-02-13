package javadev.project.consumer.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Custom JSON deserializer for flexible transaction date parsing
 * Supports two date formats from Kafka messages:
 * 1. Array format: [2026, 2, 13] - year, month, day as separate integers
 * 2. String format: "2026-02-13" - ISO date string (yyyy-MM-dd)
 * 
 * Validates date components to ensure reasonable values:
 * - Year: 1900-2100
 * - Month: 1-12
 * - Day: 1-31
 * 
 * Converts both formats to standard yyyy-MM-dd string format
 * Throws IOException if date format is invalid or unrecognized
 */
public class TransactionDateDeserializer extends JsonDeserializer<String> {

    /**
     * Deserialize JSON date value to yyyy-MM-dd string format
     * 
     * @param p    JsonParser providing access to JSON content
     * @param ctxt Deserialization context
     * @return Date string in yyyy-MM-dd format
     * @throws IOException if date format is invalid or values are out of valid
     *                     range
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isArray()) {
            // Handle array format [year, month, day]
            if (node.size() != 3) {
                throw new IOException("Invalid date array format. Expected [year, month, day]");
            }

            int year = node.get(0).asInt();
            int month = node.get(1).asInt();
            int day = node.get(2).asInt();

            // Validate date values
            if (year < 1900 || year > 2100) {
                throw new IOException("Invalid year: " + year);
            }
            if (month < 1 || month > 12) {
                throw new IOException("Invalid month: " + month);
            }
            if (day < 1 || day > 31) {
                throw new IOException("Invalid day: " + day);
            }

            // Convert to LocalDate first to validate the date
            LocalDate date = LocalDate.of(year, month, day);

            // Return as string in yyyy-MM-dd format
            return date.toString();

        } else if (node.isTextual()) {
            // Handle string format "2026-02-13"
            return node.asText();
        } else {
            throw new IOException("Invalid date format. Expected array [year, month, day] or string 'yyyy-MM-dd'");
        }
    }
}

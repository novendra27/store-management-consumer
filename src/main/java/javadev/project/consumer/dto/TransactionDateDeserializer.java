package javadev.project.consumer.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Custom deserializer for transaction_date field
 * Handles both formats:
 * - Array format: [2026, 2, 13]
 * - String format: "2026-02-13"
 */
public class TransactionDateDeserializer extends JsonDeserializer<String> {

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

package javadev.project.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO class for API responses
 * Encapsulates response message, data payload, and error details
 * Provides static factory methods for creating success and error responses
 *
 * @param <T> Type of data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {

    private String message;
    private T data;
    private String errors;

    /**
     * Create success response with data
     */
    public static <T> ResponseDto<T> success(String message, T data) {
        return ResponseDto.<T>builder()
                .message(message)
                .data(data)
                .errors(null)
                .build();
    }

    /**
     * Create success response without data
     */
    public static <T> ResponseDto<T> success(String message) {
        return ResponseDto.<T>builder()
                .message(message)
                .data(null)
                .errors(null)
                .build();
    }

    /**
     * Create error response
     */
    public static <T> ResponseDto<T> error(String message, String errors) {
        return ResponseDto.<T>builder()
                .message(message)
                .data(null)
                .errors(errors)
                .build();
    }
}

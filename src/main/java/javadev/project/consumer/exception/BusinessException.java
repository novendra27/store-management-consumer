package javadev.project.consumer.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public BusinessException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }

    /**
     * Add additional details to the exception
     */
    public BusinessException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    /**
     * Add multiple details at once
     */
    public BusinessException addDetails(Map<String, Object> details) {
        this.details.putAll(details);
        return this;
    }

    /**
     * Get formatted error message with details
     */
    public String getDetailedMessage() {
        if (details.isEmpty()) {
            return getMessage();
        }

        StringBuilder sb = new StringBuilder(getMessage());
        sb.append(" - Details: {");
        details.forEach((key, value) -> sb.append(key).append("=").append(value).append(", "));
        sb.delete(sb.length() - 2, sb.length()); // Remove last comma and space
        sb.append("}");

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("BusinessException[code=%s, message=%s, details=%s]",
                errorCode.getCode(), getMessage(), details);
    }
}

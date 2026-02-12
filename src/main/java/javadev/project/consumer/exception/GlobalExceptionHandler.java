package javadev.project.consumer.exception;

import javadev.project.consumer.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle BusinessException (our custom exception)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<Object>> handleBusinessException(BusinessException ex) {
        log.error("Business exception occurred: {} - {}", ex.getErrorCode().getCode(), ex.getDetailedMessage(), ex);

        ResponseDto<Object> response = ResponseDto.error(
                ex.getMessage(),
                String.format("[%s] %s", ex.getErrorCode().getCode(), ex.getDetailedMessage()));

        HttpStatus status = determineHttpStatus(ex.getErrorCode());

        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handle validation errors from @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error occurred: {}", ex.getMessage());

        String validationErrors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.joining(", "));

        ResponseDto<Object> response = ResponseDto.error(
                "Validation failed",
                validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle all other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ResponseDto<Object> response = ResponseDto.error(
                "An unexpected error occurred",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Determine appropriate HTTP status based on error code
     */
    private HttpStatus determineHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case PRODUCT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INSUFFICIENT_STOCK, INVALID_QUANTITY -> HttpStatus.BAD_REQUEST;
            case INVALID_TRANSACTION_DATE, INVALID_TRANSACTION_DATA,
                    EMPTY_TRANSACTION_ITEMS, VALIDATION_ERROR ->
                HttpStatus.BAD_REQUEST;
            case KAFKA_MESSAGE_PARSING_ERROR, KAFKA_CONSUMER_ERROR -> HttpStatus.UNPROCESSABLE_ENTITY;
            case DATABASE_ERROR, TRANSACTION_PROCESSING_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}

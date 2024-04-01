package cz.zcu.kiv.server.beecommunity.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler to handle when request is missing some part in request body and return error status code with error message
 */
@Slf4j
@RestControllerAdvice
public class RequestBodyMissingHandler {
    /**
     * Exception handler when http message is invalid
     * E.g. Validation in dto objects
     * @param exception exception with details about error
     * @return response entity with string message with error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidHttpMessage(HttpMessageNotReadableException exception) {
        log.error("Request fail with error: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }
}

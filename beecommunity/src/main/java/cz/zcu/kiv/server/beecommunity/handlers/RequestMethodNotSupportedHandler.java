package cz.zcu.kiv.server.beecommunity.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handler to handle when request is sent to endpoint where some method is not allowed or supported
 */
@Slf4j
@RestControllerAdvice
public class RequestMethodNotSupportedHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleInvalidHttpMessage(HttpRequestMethodNotSupportedException exception) {
        log.error("Not support method {} with error message: {}", exception.getMethod(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exception.getMessage());
    }
}

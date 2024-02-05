package cz.zcu.kiv.server.beecommunity.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestBodyMissingHandler {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleInvalidHttpMessage(HttpMessageNotReadableException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }
}

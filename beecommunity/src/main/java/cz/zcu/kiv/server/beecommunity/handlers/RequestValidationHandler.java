package cz.zcu.kiv.server.beecommunity.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Validation handler for request when some required field is missing or requirements are not fulfilled
 */
@RestControllerAdvice
public class RequestValidationHandler {
    /**
     * Method get triggered whenever there is MethodArgumentNotValidException exception.
     * @param exception object with details why exception was raised
     * @return error map with field why request is invalid
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException exception)
    {
        Map<String,String>errorMap=new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(),error.getDefaultMessage()));
        return errorMap;
    }
}
package cz.zcu.kiv.server.beecommunity.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RequestValidationHandler {
    //This method get triggered whenever there is MethodArgumentNotValidException exception.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArgument(MethodArgumentNotValidException exception)
    {
        Map<String,String>errorMap=new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errorMap.put(error.getField(),error.getDefaultMessage()));
        return errorMap;
    }

    /**
    //This method get triggered whenever there is UserAlreadyExistException exception.
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistException.class)
    public Map<String,String>handleUserNotFoundException(UserAlreadyExistException exception)
    {
        Map<String,String>errorMap=new HashMap<>();
        errorMap.put("message", exception.getMessage());
        return errorMap;
    }
    */
}
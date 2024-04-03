package cz.zcu.kiv.server.beecommunity.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RequestValidationHandlerTest {

    @InjectMocks
    private RequestValidationHandler handler;

    @Mock
    private MethodArgumentNotValidException exception;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testHandleInvalidArgument() {
        // Mock field errors
        FieldError error1 = new FieldError("firstObject", "field1", "firstObject is not valid");
        FieldError error2 = new FieldError("secondObject", "field2", "secondObject is missing");

        // Mock the behavior of the exception
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(
                List.of(new FieldError[]{error1, error2})
        );

        // Call the method under test
        Map<String, String> errorMap = handler.handleInvalidArgument(exception);

        // Verify that the error map contains the expected field errors
        assertEquals(2, errorMap.size());
        assertEquals("firstObject is not valid", errorMap.get("field1"));
        assertEquals("secondObject is missing", errorMap.get("field2"));
    }
}

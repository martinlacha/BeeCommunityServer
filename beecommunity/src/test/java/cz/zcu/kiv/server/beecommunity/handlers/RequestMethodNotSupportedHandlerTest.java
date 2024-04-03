package cz.zcu.kiv.server.beecommunity.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RequestMethodNotSupportedHandlerTest {

    @InjectMocks
    private RequestMethodNotSupportedHandler handler;

    @Mock
    private HttpRequestMethodNotSupportedException exception;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testHandleInvalidHttpMessage() {
        String errorMessage = "Unsupported method";

        // Mock the behavior of the exception
        when(exception.getMessage()).thenReturn(errorMessage);
        when(exception.getMethod()).thenReturn("POST");

        // Call the method under test
        ResponseEntity<String> responseEntity = handler.handleInvalidHttpMessage(exception);

        // Verify that the appropriate error message is returned
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
    }
}

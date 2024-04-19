package cz.zcu.kiv.server.beecommunity.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RequestBodyMissingHandlerTest {

    @InjectMocks
    private RequestBodyMissingHandler handler;

    @Mock
    private HttpMessageNotReadableException exception;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testHandleInvalidHttpMessage() {
        String errorMessage = "Invalid request body";

        // Mock the behavior of the exception
        when(exception.getMessage()).thenReturn(errorMessage);

        // Call the method under test
        ResponseEntity<String> responseEntity = handler.handleInvalidHttpMessage(exception);

        // Verify that the appropriate error message is returned
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals(errorMessage, responseEntity.getBody());
    }
}

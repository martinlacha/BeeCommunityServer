package cz.zcu.kiv.server.beecommunity.handlers;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AuthenticationUserFailureHandlerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationUserFailureHandler failureHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testOnAuthenticationFailure_UserExists() {
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setLoginAttempts(0);

        // Mocking UserRepository behavior
        when(request.getParameter(eq("username"))).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Call the method under test
        failureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Invalid credentials"));

        // Verify that the user's login attempts are incremented and saved
        verify(userRepository).saveAndFlush(userEntity);

        // Verify that the response status is set to SC_UNAUTHORIZED
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testOnAuthenticationFailure_UserDoesNotExist() {
        String email = "nonexistent@example.com";

        // Mocking UserRepository behavior
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Call the method under test
        failureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Invalid credentials"));

        // Verify that the response status is set to SC_UNAUTHORIZED
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testOnAuthenticationFailure_AccountLocked() {
        String email = "locked@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setLoginAttempts(4);

        // Mocking UserRepository behavior
        when(request.getParameter(eq("username"))).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Call the method under test
        failureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Invalid credentials"));

        // Verify that the user saved and flush
        verify(userRepository, times(1)).saveAndFlush(userEntity);

        // Verify that the response status is set to ACCOUNT_LOCKED
        verify(response).setStatus(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode());
    }
}

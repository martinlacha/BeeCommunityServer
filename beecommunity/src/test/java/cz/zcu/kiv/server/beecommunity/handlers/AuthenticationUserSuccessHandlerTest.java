package cz.zcu.kiv.server.beecommunity.handlers;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.services.IJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

class AuthenticationUserSuccessHandlerTest {

    @Mock
    private IJwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationUserSuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testOnAuthenticationSuccess_AccountNotLocked() {
        String email = "test@example.com";
        // Mocking Authentication behavior
        when(authentication.getPrincipal()).thenReturn(UserEntity.builder().email(email).loginAttempts(0).build());

        // Mocking JWT token generation
        String token = "generated-token";
        when(jwtService.generateToken(any())).thenReturn(token);

        // Call the method under test
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Verify that the user's login attempts are reset

        // Verify that the appropriate response status and header are set
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setHeader("Authorization", token);
    }

    @Test
    void testOnAuthenticationSuccess_AccountLocked() {
        String email = "locked@example.com";

        // Mocking Authentication behavior
        when(authentication.getPrincipal()).thenReturn(UserEntity.builder().email(email).loginAttempts(3).build());

        // Call the method under test
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Verify that the response status is set to ACCOUNT_LOCKED
        verify(response).setStatus(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode());
    }
}

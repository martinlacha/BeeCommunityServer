package cz.zcu.kiv.server.beecommunity.handlers;

import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;

class ApiAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AccessDeniedException accessDeniedException;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private PrintWriter printWriter;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApiAccessDeniedHandler accessDeniedHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testHandleAccessDenied() throws IOException, ServletException {
        // Mocking user details
        UserDetails userDetails = UserEntity.builder().email("test@email.com").password("123456789").build();

        // Mocking SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mocking request
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getContextPath()).thenReturn("/api/test");

        // Mocking response writer
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Call the method under test
        accessDeniedHandler.handle(request, response, accessDeniedException);

        // Verify that the response status is set to SC_FORBIDDEN
        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Check the content written to the response
        String responseContent = stringWriter.toString();
        assert(responseContent.contains("Access Denied: You do not have permission to access this resource."));
    }
}

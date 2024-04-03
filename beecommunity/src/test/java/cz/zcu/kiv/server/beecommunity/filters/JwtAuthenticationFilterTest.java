package cz.zcu.kiv.server.beecommunity.filters;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.services.IJwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private IJwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        // Add header
        request.addHeader("Authorization", "Bearer valid_token");

        // Mock UserDetails
        UserDetails userDetails = User.builder().username("test@example.com").password("password").build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // Mock JWT service
        when(jwtService.extractUsernameFromToken("valid_token")).thenReturn("test@example.com");
        when(jwtService.isTokenValid("valid_token", userDetails)).thenReturn(true);

        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify authentication is set
        verify(filterChain).doFilter(request, response);

        // Check if status code is ok
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_HeaderNull() throws Exception {
        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain is called without setting authentication
        verify(filterChain).doFilter(request, response);

        // Check if status code is ok
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_TokenWithoutBearer() throws Exception {
        // Mock Authorization header that not starts with "Bearer" string
        request.addHeader("Authorization", "InvalidTokenWithoutBearer");

        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain is called without attempting to authenticate
        verify(filterChain).doFilter(request, response);

        // Check if status code is ok
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_NullEmail() throws Exception {
        // Mock the behavior of jwtService.extractUsernameFromToken to return null
        when(jwtService.extractUsernameFromToken(anyString())).thenReturn(null);

        // Mock request with Authorization header containing a valid token
        request.addHeader("Authorization", "Bearer ValidToken");

        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain is called without attempting to authenticate
        verify(filterChain).doFilter(request, response);

        // Verify that jwtService.extractUsernameFromToken was called
        verify(jwtService).extractUsernameFromToken("ValidToken");

        // Check if status code is ok
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_UserLocked() throws Exception {
        // Mock the behavior of jwtService.extractUsernameFromToken to return a valid email
        when(jwtService.extractUsernameFromToken(anyString())).thenReturn("test@example.com");

        // Mock the behavior of userDetailsService.loadUserByUsername to return a UserDetails object
        UserDetails userDetails = User.withUsername("test@example.com")
                .password("password")
                .roles("USER")
                .accountLocked(true)
                .build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // Mock request with Authorization header containing a valid token
        request.addHeader("Authorization", "Bearer ValidToken");

        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain is called without attempting to set authentication details
        verifyNoInteractions(filterChain);

        // Verify that jwtService.extractUsernameFromToken was called
        verify(jwtService).extractUsernameFromToken("ValidToken");

        // Verify that userDetailsService.loadUserByUsername was called
        verify(userDetailsService).loadUserByUsername("test@example.com");

        // Check if status code when account is locked or not enable is set
        assertEquals(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode(), response.getStatus());

        // Verify that SecurityContextHolder contains no authentication details
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_UserNotEnabled() throws Exception {
        // Mock the behavior of jwtService.extractUsernameFromToken to return a valid email
        when(jwtService.extractUsernameFromToken(anyString())).thenReturn("test@example.com");

        // Mock the behavior of userDetailsService.loadUserByUsername to return a UserDetails object
        UserEntity userDetails = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .loginAttempts(3)
                .build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // Mock request with Authorization header containing a valid token
        request.addHeader("Authorization", "Bearer ValidToken");

        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain is called without attempting to set authentication details
        verifyNoInteractions(filterChain);

        // Verify that jwtService.extractUsernameFromToken was called
        verify(jwtService).extractUsernameFromToken("ValidToken");

        // Verify that userDetailsService.loadUserByUsername was called
        verify(userDetailsService).loadUserByUsername("test@example.com");

        // Check if status code when account is locked or not enable is set
        assertEquals(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode(), response.getStatus());

        // Verify that SecurityContextHolder contains no authentication details
        assertEquals(null, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ExpiredToken() throws Exception {
        // Mock the behavior of userDetailsService.loadUserByUsername to return a UserDetails object
        UserEntity userDetails = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .loginAttempts(2)
                .build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // Mock the behavior of jwtService.extractUsernameFromToken to throw an exception for invalid token
        when(jwtService.extractUsernameFromToken(anyString())).thenThrow(ExpiredJwtException.class);

        // Mock request with Authorization header containing an invalid token
        request.addHeader("Authorization", "Bearer ExpiredToken");

        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain is called without attempting to set authentication details
        verifyNoInteractions(filterChain);

        // Verify that jwtService.extractUsernameFromToken was called
        verify(jwtService).extractUsernameFromToken("ExpiredToken");

        // Check response status code
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        // Mock the behavior of userDetailsService.loadUserByUsername to return a UserDetails object
        UserEntity userDetails = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .loginAttempts(2)
                .build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // Mock the behavior of jwtService.extractUsernameFromToken to throw an exception for invalid token
        when(jwtService.extractUsernameFromToken(anyString())).thenReturn(userDetails.getUsername());
        when(jwtService.isTokenValid(anyString(), eq(userDetails))).thenReturn(false);

        // Mock request with Authorization header containing an invalid token
        request.addHeader("Authorization", "Bearer InvalidToken");

        // Call the filter
        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain is called without attempting to set authentication details
        verify(filterChain, times(1)).doFilter(request,response);

        // Verify that jwtService.extractUsernameFromToken was called
        verify(jwtService).extractUsernameFromToken("InvalidToken");

        // Check response status code
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
}

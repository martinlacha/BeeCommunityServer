package cz.zcu.kiv.server.beecommunity.filters;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.services.IJwtService;
import cz.zcu.kiv.server.beecommunity.services.impl.JwtServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

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
        request.addHeader("Authorization", "Bearer valid_token");
        UserEntity userDetails = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .roles(Set.of())
                .build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        when(jwtService.extractUsernameFromToken("valid_token")).thenReturn("test@example.com");
        when(jwtService.isTokenValid("valid_token", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_HeaderNull() throws Exception {
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_TokenWithoutBearer() throws Exception {
        request.addHeader("Authorization", "InvalidTokenWithoutBearer");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_NullEmail() throws Exception {
        when(jwtService.extractUsernameFromToken(anyString())).thenReturn(null);
        request.addHeader("Authorization", "Bearer ValidToken");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsernameFromToken("ValidToken");
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_ExpiredToken() {
        // Mock the behavior of userDetailsService.loadUserByUsername to return a UserDetails object
        UserEntity userDetails = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .loginAttempts(2)
                .build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.extractUsernameFromToken(anyString())).thenThrow(ExpiredJwtException.class);
        request.addHeader("Authorization", "Bearer ExpiredToken");

        filter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(filterChain);
        verify(jwtService).extractUsernameFromToken("ExpiredToken");
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        UserEntity userDetails = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .loginAttempts(2)
                .build();
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        when(jwtService.extractUsernameFromToken(anyString())).thenReturn(userDetails.getUsername());
        when(jwtService.isTokenValid(anyString(), eq(userDetails))).thenReturn(false);

        request.addHeader("Authorization", "Bearer InvalidToken");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request,response);
        verify(jwtService).extractUsernameFromToken("InvalidToken");
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    void testDoFilterInternal_Locked_AccountLocked()  {
        UserDetails userDetails = User
                .builder()
                .username("test@example.com")
                .password("password")
                .disabled(true)
                .accountLocked(true)
                .build();

        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.extractUsernameFromToken(anyString())).thenReturn(userDetails.getUsername());
        when(jwtService.isTokenValid(anyString(), eq(userDetails))).thenReturn(false);

        request.addHeader("Authorization", "Bearer InvalidToken");

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsernameFromToken("InvalidToken");
        assertEquals(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode(), response.getStatus());
    }

    @Test
    void testDoFilterInternal_ExpiredJwtException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer expired-jwt-token");
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        doThrow(new ExpiredJwtException(null, null, "JWT token has expired"))
                .when(jwtService).extractUsernameFromToken("expired-jwt-token");

        filter.doFilterInternal(request, response, new MockFilterChain());
        assert response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED;
        verify(jwtService, times(1)).extractUsernameFromToken(any());
    }
}
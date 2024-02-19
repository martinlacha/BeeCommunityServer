package cz.zcu.kiv.server.beecommunity.filters;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.services.IJwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final IJwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;
            final String userEmail;

            // Set charset and content type for response
            response.addHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.name());
            response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            if (header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = header.substring(7);
            // Get jwt token and validate
            final String jwtCopy = header.split(" ")[1].trim();

            if (!jwt.equals(jwtCopy)) {
                log.warn("Token is not valid '{}' '{}'", jwt, jwtCopy);
                filterChain.doFilter(request, response);
                return;
            }

            // Extract email
            userEmail = jwtService.extractUsernameFromToken(jwt);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if (!userDetails.isAccountNonLocked() || !userDetails.isEnabled()) {
                    log.warn("Account {} is locked: {}, enabled: {}",
                            userDetails.getUsername(), userDetails.isAccountNonLocked(), userDetails.isEnabled());
                    response.setStatus(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode());
                    return;
                }

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException expiredJwtException) {
            log.warn("{}", expiredJwtException.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (ServletException | IOException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
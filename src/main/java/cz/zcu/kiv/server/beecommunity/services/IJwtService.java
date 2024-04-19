package cz.zcu.kiv.server.beecommunity.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface IJwtService {
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
    String generateToken(UserDetails userDetails);
    String extractUsernameFromToken(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}

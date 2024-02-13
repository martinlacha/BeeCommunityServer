package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.config.PropertiesConfiguration;
import cz.zcu.kiv.server.beecommunity.services.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements IJwtService {

    private final PropertiesConfiguration propertiesConfiguration;

    /**
     * Extract username from token
     * @param token String representation of JWT token with user details
     * @return email of user
     */
    @Override
    public String extractUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generate new token for specific user details but no extra claims
     * @param userDetails user information
     * @return string value of generated token for authentication
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generate new token for specific user details with extra claims
     * @param extraClaims map with extra claims encode in token
     * @param userDetails user information
     * @return string value of generated token for authentication
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        log.info("Generating new token for {}", userDetails.getUsername());
        // Check if expiration is enabled
        int expirationTime =
                propertiesConfiguration.isEnableTokenExpiration() ?
                        propertiesConfiguration.getTokenExpirationSeconds() :
                        propertiesConfiguration.getTokenMonthExpiration();
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Check if token is still valid and it's not expired
     * @param token String value of authentication token
     * @param userDetails infromation about user from security context
     * @return token is valid return true, otherwise false
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsernameFromToken(token);
        if (propertiesConfiguration.isEnableTokenExpiration()) {
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }
        return username.equals(userDetails.getUsername());
    }

    /**
     * Check expiration time of token
     * @param token string value
     * @return token is expired returns true, otherwise false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extract expiration time from token claims
     * @param token string value of token
     * @return date of token expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract exact claims from token
     * @param token string value of token
     * @param claimsResolver function to get claim
     * @return extracted claim from token
     * @param <T> data type of claim in token
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     * @param token string value of token
     * @return map of extracted claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Getter of secret key to generate JWT
     * @return decoded secret key to generate JWT token
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(propertiesConfiguration.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

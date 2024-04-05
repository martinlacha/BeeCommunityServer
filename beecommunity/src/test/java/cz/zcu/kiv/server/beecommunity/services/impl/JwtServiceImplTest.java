package cz.zcu.kiv.server.beecommunity.services.impl;

import cz.zcu.kiv.server.beecommunity.config.PropertiesConfiguration;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.testData.TestData;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceImplTest {

    @Mock
    private PropertiesConfiguration propertiesConfiguration;

    @InjectMocks
    private JwtServiceImpl jwtService;

    private final String TEST_SECRET_KEY = "RIRrnDWGxmSIfnCHZfQAvdpaZgaffw/WchEhvWYw9wAILuL8wJ90ns+G3oKY+O7M";

    private final TestData testData = new TestData();

    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(propertiesConfiguration.getSecretKey()).thenReturn(TEST_SECRET_KEY);
        user = testData.getUser1();
    }

    @Test
    void testExtractUsernameFromToken() {
        when(propertiesConfiguration.isEnableTokenExpiration()).thenReturn(true);
        when(propertiesConfiguration.getTokenExpirationSeconds()).thenReturn(3600);
        String token = jwtService.generateToken(user);
        // Token for user Martin Lacha
        String extractedUsername = jwtService.extractUsernameFromToken(token);
        assertEquals(extractedUsername, user.getEmail());
    }

    @Test
    void testGenerateToken() {
        when(propertiesConfiguration.isEnableTokenExpiration()).thenReturn(true);
        when(propertiesConfiguration.getTokenExpirationSeconds()).thenReturn(3600);

        String token = jwtService.generateToken(new HashMap<>(), user);

        assertNotNull(token);
        verify(propertiesConfiguration).isEnableTokenExpiration();
        verify(propertiesConfiguration).getTokenExpirationSeconds();
    }

    @Test
    void testIsTokenValid() {
        when(propertiesConfiguration.isEnableTokenExpiration()).thenReturn(false);
        when(propertiesConfiguration.getTokenMonthExpiration()).thenReturn(3600);
        String token = jwtService.generateToken(new HashMap<>(), user);
        // Valid token
        assertTrue(jwtService.isTokenValid(token, user));
        verify(propertiesConfiguration, times(2)).isEnableTokenExpiration();
    }

    @Test()
    void testIsTokenExpired() {
        // Invalid expired token
        when(propertiesConfiguration.isEnableTokenExpiration()).thenReturn(true);
        when(propertiesConfiguration.getTokenExpirationSeconds()).thenReturn(0);
        String expiredToken = jwtService.generateToken(user);
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, user));
        verify(propertiesConfiguration, times(1)).isEnableTokenExpiration();
    }
}

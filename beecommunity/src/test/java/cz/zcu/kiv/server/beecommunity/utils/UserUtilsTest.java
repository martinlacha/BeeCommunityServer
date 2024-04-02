package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserUtilsTest {

    @Test
    void getUserFromSecurityContext_WhenUserAuthenticated_ReturnsUserEntity() {
        // Mocking the authenticated user
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("testUser");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Calling the method under test
        UserEntity result = UserUtils.getUserFromSecurityContext();

        // Asserting the result
        assertEquals(user, result);
    }

    @Test
    void testGetUserFromSecurityContext() {
        // Mocking the SecurityContext
        SecurityContext securityContextMock = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContextMock);

        // Creating a dummy user entity
        UserEntity dummyUser = new UserEntity();
        dummyUser.setId(1L);
        dummyUser.setEmail("testuser");

        // Mocking the Principal object returned by Authentication
        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(dummyUser);

        // Setting up the mock behavior for the SecurityContext to return the authenticationMock
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);

        // Calling the method under test
        UserEntity result = UserUtils.getUserFromSecurityContext();

        // Assertions
        assertEquals(dummyUser, result);
    }

}

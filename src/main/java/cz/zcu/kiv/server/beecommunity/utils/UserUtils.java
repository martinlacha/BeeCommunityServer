package cz.zcu.kiv.server.beecommunity.utils;

import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {
    private UserUtils() {}

    /**
     * Get UserDetails from security context if user is already authenticated
     * @return user details from security context
     */
    public static UserEntity getUserFromSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return (UserEntity) authentication.getPrincipal();
    }


}

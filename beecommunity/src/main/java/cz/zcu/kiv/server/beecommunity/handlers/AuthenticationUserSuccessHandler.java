package cz.zcu.kiv.server.beecommunity.handlers;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.services.IJwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Authentication handler which handling when user is successfully login by email and password
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationUserSuccessHandler implements AuthenticationSuccessHandler {

    private final IJwtService jwtService;

    /**
     * Method called on successful authentication and check the account is not locked
     * Login attempts are reset and new JWT token is generated for this account
     * @param request object from user with details
     * @param response object that will be return to user with new JWT authentication token
     * @param authentication object with details
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String email = request.getParameter("email");
        log.info("User {} succesfully login", email);
        UserEntity user = (UserEntity) authentication.getPrincipal();
        if (!user.isAccountNonLocked()) {
            response.setStatus(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode());
            return;
        }
        user.setLoginAttempts(0);
        var token = jwtService.generateToken(user);
        response.setStatus(user.isNewAccount() ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_OK);
        response.setHeader("Authorization", token);
    }
}

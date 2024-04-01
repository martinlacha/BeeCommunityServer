package cz.zcu.kiv.server.beecommunity.handlers;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Authentication handler which handling when user is unsuccessfully login by email and password
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationUserFailureHandler implements AuthenticationFailureHandler {

    private final UserRepository userRepository;

    /**
     * Method called on unsuccessful authentication
     * Increase login attempts if account exists
     * @param request object from user with details
     * @param response object that will be return to user
     * @param exception object with details why authentication was unsuccessful
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String email = request.getParameter("username");
        log.info("Authentication failed for user: {}", email);
        Optional<UserEntity> user = userRepository.findByEmail(email);

        user.ifPresentOrElse(
            userEntity -> {
                userEntity.setLogin_attempts(userEntity.getLogin_attempts() + 1);
                userRepository.saveAndFlush(userEntity);
                if (!userEntity.isAccountNonLocked()) {
                    response.setStatus(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode());
                    return;
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            },
            () -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        );
    }
}

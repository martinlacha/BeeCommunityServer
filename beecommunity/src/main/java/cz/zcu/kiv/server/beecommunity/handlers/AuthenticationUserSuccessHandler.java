package cz.zcu.kiv.server.beecommunity.handlers;

import cz.zcu.kiv.server.beecommunity.enums.ResponseStatusCodes;
import cz.zcu.kiv.server.beecommunity.jpa.entity.UserEntity;
import cz.zcu.kiv.server.beecommunity.jpa.repository.UserRepository;
import cz.zcu.kiv.server.beecommunity.services.IJwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationUserSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final IJwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = request.getParameter("email");
        log.info("User {} succesfully login", email);
        UserEntity user = (UserEntity) authentication.getPrincipal();
        if (!user.isAccountNonLocked()) {
            response.setStatus(ResponseStatusCodes.ACCOUNT_LOCKED_STATUS_CODE.getCode());
            return;
        }
        user.setLogin_attempts(0);
        var token = jwtService.generateToken(user);
        response.setStatus(user.isNewAccount() ? HttpServletResponse.SC_CREATED : HttpServletResponse.SC_OK);
        response.setHeader("Authorization", token);
    }
}

package cz.zcu.kiv.server.beecommunity.handlers;

import cz.zcu.kiv.server.beecommunity.utils.UserUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {
        var user = UserUtils.getUserFromSecurityContext();
        log.warn("Access denied for user:{} endpoint: {}:{}", user.getFullName(), request.getMethod(), request.getRequestURI().substring(request.getContextPath().length()));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Access Denied: You do not have permission to access this resource.");
    }
}
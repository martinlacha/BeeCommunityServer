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

/**
 * Handler for access denied when user has no permission for specific operation.
 * It can cause e.g. missing admin role
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * Handle method when access will be denied for some operation
     * @param request request from user
     * @param response response return to user
     * @param accessDeniedException exception with details about error
     * @throws IOException exception
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {
        var user = UserUtils.getUserFromSecurityContext();
        log.warn("Access denied for user:{} endpoint: {}:{}",
                user.getFullName(),
                request.getMethod(),
                request.getRequestURI().substring(request.getContextPath().length()));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Access Denied: You do not have permission to access this resource.");
    }
}
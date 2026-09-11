package com.example.serbisyofullstack.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns clean JSON 401/403 responses for stateless API callers instead of
 * redirecting or rendering an error page.
 */
@Component
public class RestAuthenticationHandlers {

    private static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}".formatted(
                        status, status == 401 ? "Unauthorized" : "Forbidden", message));
    }

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response,
                AuthenticationException authException)
                -> writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.access.AccessDeniedException accessDeniedException)
                -> writeError(response, HttpServletResponse.SC_FORBIDDEN, "Access denied");
    }
}

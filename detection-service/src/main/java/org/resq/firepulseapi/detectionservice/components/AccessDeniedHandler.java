package org.resq.firepulseapi.detectionservice.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    public AccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            @NonNull HttpServletRequest request,
            HttpServletResponse response,
            @NonNull AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You are not allowed to perform this action."
        );
        problem.setTitle("Access Denied");

        objectMapper.writeValue(response.getWriter(), problem);
    }
}

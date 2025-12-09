package org.resq.firepulseapi.configservice.components;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) {
        long startTime = System.currentTimeMillis();

        MDC.put("requestStartTime", String.valueOf(startTime));

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler, Exception ex) {
        long endTime = System.currentTimeMillis();
        long duration = endTime - Long.parseLong(MDC.get("requestStartTime"));

        String uriWithQuery = request.getRequestURI();
        String query = request.getQueryString();

        if (query != null && !query.isEmpty()) {
            uriWithQuery = uriWithQuery + "?" + query;
        }

        logger.info("Request: {} {} {} ({}ms)", request.getMethod(), uriWithQuery, response == null ? "xxx" : response.getStatus(), duration);

        MDC.clear();
    }
}

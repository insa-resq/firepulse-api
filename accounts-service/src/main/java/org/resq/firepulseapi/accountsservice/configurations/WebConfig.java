package org.resq.firepulseapi.accountsservice.configurations;

import org.jspecify.annotations.NonNull;
import org.resq.firepulseapi.accountsservice.annotations.AuthenticatedUserRole;
import org.resq.firepulseapi.accountsservice.components.RequestLoggingInterceptor;
import org.resq.firepulseapi.accountsservice.entities.enums.UserRole;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RequestLoggingInterceptor requestLoggingInterceptor;

    public WebConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    static {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthenticatedUserRole.class);
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/health/readiness");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(@NonNull MethodParameter parameter) {
                return parameter.getParameterType().equals(UserRole.class)
                        && parameter.hasParameterAnnotation(AuthenticatedUserRole.class);
            }

            @Override
            public Object resolveArgument(
                    @NonNull MethodParameter parameter,
                    ModelAndViewContainer mavContainer,
                    @NonNull NativeWebRequest webRequest,
                    WebDataBinderFactory binderFactory
            ) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
                    return null;
                }

                String rolesString = jwt.getClaimAsString("roles");
                if (rolesString == null || rolesString.isBlank()) return null;

                // Parse roles
                List<UserRole> foundRoles = Arrays.stream(rolesString.split(" "))
                        .map(this::safeValueOf)
                        .filter(Objects::nonNull)
                        .toList();

                // Assume the first valid role is the user's role (only one role per user is expected)
                return foundRoles.isEmpty() ? null : foundRoles.getFirst();
            }

            // Helper to avoid crashing on unknown roles (like "FACTOR_PASSWORD")
            private UserRole safeValueOf(String role) {
                try {
                    return UserRole.valueOf(role);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });
    }
}

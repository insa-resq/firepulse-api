package org.resq.firepulseapi.accountsservice.exceptions;

import feign.FeignException;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation Failed");
        problem.setTitle("Invalid Input");

        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String message = error.getDefaultMessage();

            if (error.isBindingFailure()) {
                try {
                    // 1. Get the class of the DTO
                    Object target = ex.getBindingResult().getTarget();
                    Class<?> targetClass = (target != null) ? target.getClass() : null;

                    if (targetClass != null) {
                        // 2. Find the field in the class
                        Field field = targetClass.getDeclaredField(error.getField());

                        // 3. Resolve the type using Spring's utility
                        ResolvableType resolvableType = ResolvableType.forField(field);
                        Class<?> resolvedClass = resolvableType.resolve();

                        if (resolvedClass != null) {
                            Class<?> enumType = null;

                            // Case A: It's a direct Enum
                            if (resolvedClass.isEnum()) {
                                enumType = resolvedClass;
                            }
                            // Case B: It's a Collection/List of Enums
                            else if (Collection.class.isAssignableFrom(resolvedClass)) {
                                // Extract the Generic argument (the content of the list)
                                Class<?> genericType = resolvableType.getGeneric(0).resolve();
                                if (genericType != null && genericType.isEnum()) {
                                    enumType = genericType;
                                }
                            }

                            // 4. Generate the message if we found an Enum
                            if (enumType != null) {
                                List<String> allowedValues = Arrays.stream(enumType.getEnumConstants())
                                        .map(Object::toString)
                                        .collect(Collectors.toList());

                                // Clean up the rejected value (remove brackets if Spring added them)
                                String rejectedFieldValue = String.valueOf(error.getRejectedValue());

                                // Split rejectedValue by comma and take the elements not matching allowed values
                                List<String> wrongValues = Arrays.stream(rejectedFieldValue.split(","))
                                        .map(String::trim)
                                        .filter(val -> !allowedValues.contains(val))
                                        .collect(Collectors.toList());

                                message = String.format(
                                        "Invalid values [%s]. Allowed values are: [%s]",
                                        String.join(", ", wrongValues),
                                        String.join(", ", allowedValues)
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    message = "Invalid format for field '" + error.getField() + "'";
                }
            }

            errors.put(error.getField(), message);
        }

        problem.setProperty("errors", errors);

        return createResponseEntity(problem, headers, status, request);
    }

    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApiException(ApiException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(exception.getStatus(), exception.getMessage());
        problemDetail.setTitle("API Error");
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException exception) {
        logger.warn("Security Event (AuthN): {}", exception.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed. Please check your credentials."
        );
        problemDetail.setTitle("Unauthorized");
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        logger.warn("Security Event (AuthZ): {}", exception.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "You are not allowed to perform this action."
        );
        problemDetail.setTitle("Access Denied");
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception exception) {
        logger.error("Internal Error: ", exception);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal server error occurred."
        );
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }

    @ExceptionHandler(FeignException.class)
    public ProblemDetail handleGeneralFeign(FeignException exception) {
        logger.error("External service error [{}]: {}", exception.status(), exception.contentUTF8());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                "An unexpected error occurred while communicating with an external service."
        );
        problemDetail.setTitle("External Service Error");
        return problemDetail;
    }
}

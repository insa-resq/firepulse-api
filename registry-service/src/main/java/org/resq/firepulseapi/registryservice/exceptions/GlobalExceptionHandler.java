package org.resq.firepulseapi.registryservice.exceptions;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
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
                    Object target = ex.getBindingResult().getTarget();
                    Class<?> targetClass = (target != null) ? target.getClass() : null;

                    if (targetClass != null) {
                        String fieldPath = error.getField();
                        Class<?> currentClass = targetClass;
                        ResolvableType currentType;

                        // If the target is a List, try to find the DTO class from the generic type
                        if (Collection.class.isAssignableFrom(targetClass)) {
                            ResolvableType genericType = ResolvableType.forInstance(target).getGeneric(0);
                            if (genericType != ResolvableType.NONE) {
                                currentType = genericType;
                                currentClass = currentType.resolve();
                            }
                        }

                        // Handle nested field paths like "vehicles[0].type" or "address.city"
                        String[] parts = fieldPath.split("\\.");
                        for (String part : parts) {
                            if (currentClass == null) break;

                            // Handle indexed fields like "vehicles[0]"
                            String fieldName = part.replaceAll("\\[\\d+]", "");
                            Field field = currentClass.getDeclaredField(fieldName);
                            currentType = ResolvableType.forField(field);
                            currentClass = currentType.resolve();

                            // If it's a collection, move to the element type
                            if (currentClass != null && Collection.class.isAssignableFrom(currentClass)) {
                                currentType = currentType.getGeneric(0);
                                currentClass = currentType.resolve();
                            }
                        }

                        if (currentClass != null && currentClass.isEnum()) {
                            List<String> allowedValues = Arrays.stream(currentClass.getEnumConstants())
                                    .map(Object::toString)
                                    .collect(Collectors.toList());

                            String rejectedFieldValue = String.valueOf(error.getRejectedValue());
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
}

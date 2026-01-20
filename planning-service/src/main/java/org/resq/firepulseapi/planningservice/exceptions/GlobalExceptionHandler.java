package org.resq.firepulseapi.planningservice.exceptions;

import feign.FeignException;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
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

        // The target object (the DTO) is readily available here
        Object target = ex.getBindingResult().getTarget();
        Class<?> targetClass = (target != null) ? target.getClass() : null;

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String message = error.getDefaultMessage();
            String fieldName = error.getField();

            // Try to enhance the message if it's a binding failure (like bad Enum)
            if (error.isBindingFailure() && targetClass != null) {
                String enhancedMessage = tryResolveEnumValidationMessage(targetClass, fieldName, error.getRejectedValue());
                if (enhancedMessage != null) {
                    message = enhancedMessage;
                }
            }
            errors.put(fieldName, message);
        }

        problem.setProperty("errors", errors);
        return createResponseEntity(problem, headers, status, request);
    }

    // -------------------------------------------------------------------------
    // 2. Handle List/Collection Validation (@Valid @RequestBody List<Dto> dtos)
    // -------------------------------------------------------------------------
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation Failed");
        problem.setTitle("Invalid Input");

        Map<String, String> errors = new LinkedHashMap<>();

        // Iterate over parameters that failed validation (e.g., the List<Dto>)
        ex.getParameterValidationResults().forEach(parameterResult -> {
            MethodParameter methodParam = parameterResult.getMethodParameter();

            // Determine the DTO class type from the method parameter
            Class<?> targetClass;
            if (Collection.class.isAssignableFrom(methodParam.getParameterType())) {
                // If param is List<Dto>, get Dto.class
                targetClass = methodParam.nested().getNestedParameterType();
            } else {
                targetClass = methodParam.getParameterType();
            }

            // Iterate over the specific errors for this parameter
            Class<?> finalTargetClass = targetClass;
            parameterResult.getResolvableErrors().forEach(error -> {
                String fieldName;
                String message = error.getDefaultMessage();
                Object rejectedValue;

                if (error instanceof FieldError fieldError) {
                    // Extract field name. In lists, it might look like "list[0].stationId" or just "stationId"
                    // We need the simple name "stationId" for reflection
                    String fullPath = fieldError.getField();
                    fieldName = fullPath;
                    rejectedValue = fieldError.getRejectedValue();

                    // Logic to strip array notation (e.g., "[0].type" -> "type") for reflection lookup
                    String simpleFieldName = fullPath.contains(".")
                            ? fullPath.substring(fullPath.lastIndexOf('.') + 1)
                            : fullPath;

                    if (fieldError.isBindingFailure()) {
                        String enhancedMessage = tryResolveEnumValidationMessage(finalTargetClass, simpleFieldName, rejectedValue);
                        if (enhancedMessage != null) {
                            message = enhancedMessage;
                        }
                    }
                } else {
                    // Fallback if it's not a field error (e.g., parameter level constraint)
                    fieldName = methodParam.getParameterName();
                }

                errors.put(fieldName, message);
            });
        });

        problem.setProperty("errors", errors);
        return createResponseEntity(problem, headers, status, request);
    }

    // -------------------------------------------------------------------------
    // Shared Helper: Logic to Detect Enums and generate "Allowed values" message
    // -------------------------------------------------------------------------
    private String tryResolveEnumValidationMessage(Class<?> targetClass, String fieldName, Object rejectedValue) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
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
                    Class<?> genericType = resolvableType.getGeneric(0).resolve();
                    if (genericType != null && genericType.isEnum()) {
                        enumType = genericType;
                    }
                }

                if (enumType != null) {
                    List<String> allowedValues = Arrays.stream(enumType.getEnumConstants())
                            .map(Object::toString)
                            .collect(Collectors.toList());

                    // Clean up rejected value
                    String rejectedStr = String.valueOf(rejectedValue);

                    // Identify which specific values were wrong
                    List<String> wrongValues = Arrays.stream(rejectedStr.split(","))
                            .map(String::trim)
                            .filter(val -> !allowedValues.contains(val))
                            .collect(Collectors.toList());

                    // If we couldn't parse specific wrong values, just show the whole thing
                    String wrongValuesStr = wrongValues.isEmpty() ? rejectedStr : String.join(", ", wrongValues);

                    return String.format(
                            "Invalid values [%s]. Allowed values are: [%s]",
                            wrongValuesStr,
                            String.join(", ", allowedValues)
                    );
                }
            }
        } catch (Exception e) {
            // If reflection fails (field not found, etc.), return null to use the default message
            return null;
        }
        return null;
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

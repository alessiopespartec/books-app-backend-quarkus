package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.alessio.response.CustomResponse;

import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<jakarta.validation.ConstraintViolationException> {

    @Override
    public Response toResponse(jakarta.validation.ConstraintViolationException exception) {
        // Get each property name and its message (set in @NotBlank value)
        String errorMessage = exception.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .collect(Collectors.joining(", ")); // joined with comma (", ")

        CustomResponse customResponse = new CustomResponse(
                true,
                "Validation error",
                errorMessage,
                null,
                400
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(customResponse)
                .build();
    }
}
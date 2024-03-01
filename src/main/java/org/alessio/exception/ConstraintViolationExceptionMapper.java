package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<jakarta.validation.ConstraintViolationException> {

    @Override
    public Response toResponse(jakarta.validation.ConstraintViolationException exception) {
        /*
        Estrae per ogni proprietà violata il suo nome e il messaggio errore nell'entità
        concatenandolo con la virgola.
        Ad esempio "firstName cannot be blank, lastName cannot be blank".
        |   |   |   |   |   |   |
        |   |   |   |   |   |   |
        V   V   V   V   V   V   V
        */
        String errorMessage = exception.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .collect(Collectors.joining(", "));

        ErrorPayload payload = new ErrorPayload("Validation error", errorMessage);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(payload)
                .build();
    }
}
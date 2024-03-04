package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidParameterExceptionMapper implements ExceptionMapper<InvalidParameterException> {

    @Override
    public Response toResponse(InvalidParameterException exception) {
        ErrorPayload payload = new ErrorPayload("Invalid Parameter", exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(payload).build();
    }
}

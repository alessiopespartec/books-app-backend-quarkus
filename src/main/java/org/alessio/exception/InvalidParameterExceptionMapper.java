package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.alessio.response.CustomResponse;

@Provider
public class InvalidParameterExceptionMapper implements ExceptionMapper<InvalidParameterException> {

    @Override
    public Response toResponse(InvalidParameterException exception) {
        CustomResponse customResponse = new CustomResponse(
                true,
                "Invalid Parameter",
                exception.getMessage(),
                null,
                400
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(customResponse)
                .build();
    }
}

package org.alessio.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.alessio.response.CustomResponse;
import org.hibernate.exception.DataException;

@Provider
public class DataExceptionMapper implements ExceptionMapper<DataException> {

    @Override
    public Response toResponse(DataException exception) {
        CustomResponse customResponse = new CustomResponse(
                true,
                "Validation data error",
                "One or more fields exceed the maximum length allowed",
                null,
                400
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(customResponse)
                .build();
    }
}

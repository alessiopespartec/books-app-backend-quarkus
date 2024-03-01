package org.alessio.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorPayload {
    public String error;
    public String message;

    public ErrorPayload(String error, String message) {
        this.error = error;
        this.message = message;
    }
}

package org.alessio.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse {
    public boolean isError;
    public String errorName;
    public String message;
    public Object data;
    public Integer status;

    public CustomResponse(String errorName, String message) {
        this.errorName = errorName;
        this.message = message;
    }

    @JsonProperty("isError")
    public boolean isError() {
        return isError;
    }
}

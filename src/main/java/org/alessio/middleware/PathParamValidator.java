package org.alessio.middleware;

import org.alessio.exception.InvalidParameterException;

public class PathParamValidator {

    public static Long validateAndConvert(String param) {
        try {
            long validId = Long.parseLong(param);
            if (validId < 0) {
                throw new InvalidParameterException("ID must be a positive number");
            }
            if (validId == 0) {
                throw new InvalidParameterException("ID cannot be zero");
            }
            return validId;
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("ID must be a numeric value");
        }
    }
}

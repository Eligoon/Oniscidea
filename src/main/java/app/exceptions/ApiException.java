package app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiException extends RuntimeException {

    private static final Logger logger =
            LoggerFactory.getLogger(ApiException.class);

    private final int code;

    public ApiException(
            int code,
            String msg
    ) {
        super(msg);

        this.code = code;

        if (code >= 500) {
            logger.error(
                    "ApiException (code={}): {}",
                    code,
                    msg
            );
        } else {
            logger.warn(
                    "ApiException (code={}): {}",
                    code,
                    msg
            );
        }
    }

    public ApiException(
            int code,
            String msg,
            Throwable cause
    ) {
        super(msg, cause);

        this.code = code;

        if (code >= 500) {
            logger.error(
                    "ApiException (code={}): {}",
                    code,
                    msg,
                    cause
            );
        } else {
            logger.warn(
                    "ApiException (code={}): {}",
                    code,
                    msg,
                    cause
            );
        }
    }

    public int getCode() {
        return code;
    }
}
package dev.vality.fistful.reporter.exception;

public class PartyNotFoundException extends PartyManagementClientException {

    public PartyNotFoundException(String message) {
        super(message);
    }

    public PartyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PartyNotFoundException(Throwable cause) {
        super(cause);
    }

    public PartyNotFoundException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

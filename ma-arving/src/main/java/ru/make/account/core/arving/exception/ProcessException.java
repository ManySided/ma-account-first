package ru.make.account.core.arving.exception;

/**
 * Исключение возникающие в бизнес логике
 */
public class ProcessException extends RuntimeException {
    public ProcessException() {
    }

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessException(Throwable cause) {
        super(cause);
    }

    public ProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package net.stawrul.services.exceptions;

/**
 * @author Dominik Kinal <kinaldominik@gmail.com>
 */
public class ValidationException extends RuntimeException {

    public ValidationException() {
        super("Unknown validation exception");
    }

    public ValidationException(String msg) {
        super(msg);
    }
}

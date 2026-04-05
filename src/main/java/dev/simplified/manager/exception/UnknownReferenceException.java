package dev.simplified.manager.exception;

import dev.simplified.manager.Manager;

/**
 * Thrown when attempting to access or modify a key that does not exist in a
 * {@link Manager}.
 * <p>
 * This indicates that the requested identifier was never registered. Use
 * {@link Manager#isRegistered(Object)} to check for existence
 * before performing operations that may throw this exception.
 *
 * @see Manager#get(Object)
 * @see Manager#remove(Object)
 * @see Manager#update(Object, Object)
 */
public final class UnknownReferenceException extends ManagerException {

    /**
     * Creates a new {@code UnknownReferenceException} for the given identifier.
     *
     * @param identifier the key that was not found in the manager
     */
    public UnknownReferenceException(Object identifier) {
        super("Reference '%s' has not been registered", identifier);
    }

    /**
     * Returns the formatted error message for the given identifier without
     * constructing an exception instance.
     *
     * @param identifier the key that was not found
     * @return the formatted error message
     */
    public static String getMessage(Object identifier) {
        return String.format("Reference '%s' has not been registered", identifier);
    }

}

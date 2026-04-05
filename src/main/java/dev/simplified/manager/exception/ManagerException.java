package dev.simplified.manager.exception;

import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown when the manager layer encounters a registration, reference, or
 * permission error.
 *
 * @see RegisteredReferenceException
 * @see UnknownReferenceException
 * @see InsufficientModeException
 */
public class ManagerException extends RuntimeException {

    /**
     * Constructs a new {@code ManagerException} with the specified cause.
     *
     * @param cause the underlying throwable that caused this exception
     */
    public ManagerException(@NotNull Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code ManagerException} with the specified detail message.
     *
     * @param message the detail message
     */
    public ManagerException(@NotNull String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ManagerException} with the specified cause and detail message.
     *
     * @param cause the underlying throwable that caused this exception
     * @param message the detail message
     */
    public ManagerException(@NotNull Throwable cause, @NotNull String message) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code ManagerException} with a formatted detail message.
     *
     * @param message the format string
     * @param args the format arguments
     */
    public ManagerException(@NotNull @PrintFormat String message, @Nullable Object... args) {
        super(String.format(message, args));
    }

    /**
     * Constructs a new {@code ManagerException} with the specified cause and a formatted detail message.
     *
     * @param cause the underlying throwable that caused this exception
     * @param message the format string
     * @param args the format arguments
     */
    public ManagerException(@NotNull Throwable cause, @NotNull @PrintFormat String message, @Nullable Object... args) {
        super(String.format(message, args), cause);
    }

}

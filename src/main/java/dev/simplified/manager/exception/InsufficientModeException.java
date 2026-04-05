package dev.simplified.manager.exception;

import dev.simplified.manager.Manager;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when a mutating operation is attempted on a {@link Manager} whose current
 * {@link Manager.Mode} does not permit that operation.
 * <p>
 * For example, calling {@link Manager#clear()} on a manager with {@link Manager.Mode#NORMAL}
 * will throw this exception because {@code NORMAL} does not allow removals.
 *
 * @see Manager.Mode
 */
public final class InsufficientModeException extends ManagerException {

    /**
     * Creates a new {@code InsufficientModeException} for the given mode.
     *
     * @param mode the current mode that was insufficient for the attempted operation
     */
    public InsufficientModeException(@NotNull Manager.Mode mode) {
        super(String.format("Manager mode '%s' is insufficient to perform this action", mode.name()));
    }

}

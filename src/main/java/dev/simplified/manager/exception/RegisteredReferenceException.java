package dev.simplified.manager.exception;

import dev.simplified.manager.Manager;
import org.jetbrains.annotations.NotNull;

/**
 * Thrown when attempting to register a key that is already present in a
 * {@link Manager}.
 * <p>
 * This prevents accidental overwrites of existing entries. To replace a value,
 * use the manager's {@code update} method instead (requires at least
 * {@link Manager.Mode#UPDATE}).
 *
 * @see Manager#add(Object, Object)
 */
public final class RegisteredReferenceException extends ManagerException {

    /**
     * Creates a new {@code RegisteredReferenceException} for the given identifier.
     *
     * @param identifier the key that was already registered
     */
    public RegisteredReferenceException(@NotNull Object identifier) {
        super(String.format("Reference '%s' is already registered", identifier));
    }

}

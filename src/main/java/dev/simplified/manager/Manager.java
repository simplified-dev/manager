package dev.simplified.manager;

import dev.simplified.manager.exception.InsufficientModeException;
import dev.simplified.manager.exception.RegisteredReferenceException;
import dev.simplified.manager.exception.UnknownReferenceException;
import dev.simplified.collection.concurrent.Concurrent;
import dev.simplified.collection.concurrent.ConcurrentMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Abstract base class for all manager types, providing a thread-safe, mode-controlled
 * registry of key-value pairs backed by a {@link ConcurrentMap}.
 * <p>
 * Subclasses define the concrete key and value types, expose public access methods, and
 * supply a {@code keyMatcher} function that determines how entries are matched during
 * lookups. The {@link Mode} controls which mutating operations are permitted:
 * <ul>
 *   <li>{@link Mode#NORMAL} - add only (no updates or removals)</li>
 *   <li>{@link Mode#UPDATE} - add and update (no removals)</li>
 *   <li>{@link Mode#ALL} - add, update, and remove</li>
 * </ul>
 *
 * @param <K> the type of keys maintained by this manager
 * @param <V> the type of mapped values
 * @see KeyManager
 * @see ServiceManager
 */
@RequiredArgsConstructor
public abstract class Manager<K, V> {

    private final transient @NotNull ConcurrentMap<K, V> ref = Concurrent.newMap();
    private final transient @NotNull BiFunction<Map.Entry<K, V>, K, Boolean> keyMatcher;
    @Getter private final transient @NotNull Mode mode;

    /**
     * Registers a new key-value pair in the manager.
     * <p>
     * If the identifier is already registered, a {@link RegisteredReferenceException} is thrown.
     *
     * @param identifier the key to register
     * @param value the value to associate with the key
     * @throws RegisteredReferenceException if {@code identifier} is already registered
     */
    protected void add(@NotNull K identifier, @NotNull V value) throws RegisteredReferenceException {
        if (this.isRegistered(identifier))
            throw new RegisteredReferenceException(identifier);

        this.ref.put(identifier, value);
    }

    /**
     * Removes all existing key-value pairs from the manager.
     * <p>
     * This operation requires {@link Mode#ALL}; calling it with a lower mode
     * throws an {@link InsufficientModeException}.
     *
     * @throws InsufficientModeException if the current mode is below {@link Mode#ALL}
     */
    public final void clear() throws InsufficientModeException {
        if (this.getMode().getLevel() < Mode.ALL.getLevel())
            throw new InsufficientModeException(this.getMode());

        this.ref.clear();
    }

    /**
     * Retrieves the value for the given identifier as an {@link Optional}.
     * <p>
     * Returns {@link Optional#empty()} if no matching entry is found. The lookup
     * uses the {@code keyMatcher} function provided at construction time.
     *
     * @param identifier the key to look up
     * @return an {@link Optional} containing the value if found, otherwise empty
     */
    protected @NotNull Optional<V> getOptional(@NotNull K identifier) {
        return this.ref.stream()
            .filter(entry -> this.keyMatcher.apply(entry, identifier))
            .map(Map.Entry::getValue)
            .findFirst();
    }

    /**
     * Retrieves the value for the given identifier, throwing if not found.
     * <p>
     * This is the strict counterpart to {@link #getOptional(Object)}. It delegates
     * to {@code getOptional} and throws an {@link UnknownReferenceException} if the
     * result is empty.
     *
     * @param identifier the key to look up
     * @return the value associated with the key
     * @throws UnknownReferenceException if {@code identifier} is not registered
     * @see #isRegistered(Object)
     */
    protected @NotNull V get(@NotNull K identifier) throws UnknownReferenceException {
        return this.getOptional(identifier).orElseThrow(() -> new UnknownReferenceException(identifier));
    }

    /**
     * Checks whether the given identifier has a registered entry in the manager.
     *
     * @param identifier the key to check
     * @return {@code true} if a matching entry exists, {@code false} otherwise
     */
    public final boolean isRegistered(@NotNull K identifier) {
        return this.ref.stream().anyMatch(entry -> this.keyMatcher.apply(entry, identifier));
    }

    /**
     * Removes an existing key-value pair from the manager.
     * <p>
     * The identifier must already be registered and the current mode must be
     * {@link Mode#ALL}; otherwise the appropriate exception is thrown.
     *
     * @param identifier the key to remove
     * @throws UnknownReferenceException if {@code identifier} is not registered
     * @throws InsufficientModeException if the current mode is below {@link Mode#ALL}
     */
    protected void remove(@NotNull K identifier) throws InsufficientModeException, UnknownReferenceException {
        if (!this.isRegistered(identifier))
            throw new UnknownReferenceException(identifier);
        else {
            if (this.getMode().getLevel() < Mode.ALL.getLevel())
                throw new InsufficientModeException(this.getMode());
        }

        this.ref.remove(identifier);
    }

    /**
     * Registers or replaces a key-value pair.
     * <p>
     * If the identifier is not yet registered, it is added; if it is already registered,
     * the existing value is replaced.
     * <p>
     * Replacing values requires at least {@link Mode#UPDATE}.
     *
     * @param identifier the key to register or replace
     * @param value the value to associate with the key
     * @throws InsufficientModeException if the current mode is below {@link Mode#UPDATE}
     */
    protected void put(@NotNull K identifier, @NotNull V value) throws InsufficientModeException {
        if (this.isRegistered(identifier)) {
            if (this.getMode().getLevel() < Mode.UPDATE.getLevel())
                throw new InsufficientModeException(this.getMode());
        }

        this.ref.put(identifier, value);
    }

    /**
     * Updates the value for an existing key-value pair.
     * <p>
     * The identifier must already be registered and the current mode must be at least
     * {@link Mode#UPDATE}; otherwise the appropriate exception is thrown.
     *
     * @param identifier the key whose value should be replaced
     * @param newValue the new value to associate with the key
     * @throws UnknownReferenceException if {@code identifier} is not registered
     * @throws InsufficientModeException if the current mode is below {@link Mode#UPDATE}
     */
    protected void update(@NotNull K identifier, @NotNull V newValue) throws InsufficientModeException, UnknownReferenceException {
        if (!this.isRegistered(identifier))
            throw new UnknownReferenceException(identifier);
        else {
            if (this.getMode().getLevel() < Mode.UPDATE.getLevel())
                throw new InsufficientModeException(this.getMode());
        }

        this.ref.put(identifier, newValue);
    }

    /**
     * Controls which mutating operations a {@link Manager} instance permits.
     * <p>
     * Each mode has a numeric level; operations compare their required level against
     * the manager's current mode level to determine if they are allowed.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Mode {

        /**
         * The manager may only add new entries. Updates and removals are prohibited.
         */
        NORMAL(0, false, false),

        /**
         * The manager may add new entries and update existing ones. Removals are prohibited.
         */
        UPDATE(1, true, false),

        /**
         * The manager may add, update, and remove entries without restriction.
         */
        ALL(2, true, true);

        /** The numeric level used for permission comparisons. */
        private final int level;

        /** Whether this mode permits updating existing entries. */
        private final boolean updateEnabled;

        /** Whether this mode permits removing existing entries. */
        private final boolean removeEnabled;

    }

}

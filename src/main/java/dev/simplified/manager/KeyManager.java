package dev.simplified.manager;

import dev.simplified.manager.exception.InsufficientModeException;
import dev.simplified.manager.exception.RegisteredReferenceException;
import dev.simplified.manager.exception.UnknownReferenceException;
import dev.simplified.collection.tuple.pair.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * A concrete string-keyed manager for storing named values such as API keys, tokens,
 * and other configuration entries.
 * <p>
 * All keys and values are {@link String}s. Key matching is determined by the
 * {@link BiFunction} provided at construction time (typically case-insensitive
 * equality). This manager is intended to be instantiated once in a central
 * location and shared across the application.
 *
 * <pre>{@code
 * KeyManager keys = new KeyManager(
 *     (entry, key) -> key.equalsIgnoreCase(entry.getKey()),
 *     Mode.UPDATE
 * );
 * keys.add("HYPIXEL_API_KEY", apiKeyValue);
 * String value = keys.get("HYPIXEL_API_KEY");
 * }</pre>
 *
 * @see Manager
 */
public class KeyManager extends Manager<String, String> {

    /**
     * Creates a new {@code KeyManager} with the given key matcher and {@link Mode#NORMAL} mode.
     *
     * @param keyMatcher a function that determines whether a map entry matches a given key
     */
    public KeyManager(@NotNull BiFunction<Map.Entry<String, String>, String, Boolean> keyMatcher) {
        this(keyMatcher, Mode.NORMAL);
    }

    /**
     * Creates a new {@code KeyManager} with the given key matcher and mode.
     *
     * @param keyMatcher a function that determines whether a map entry matches a given key
     * @param mode the mutability mode controlling which operations are permitted
     * @see Mode
     */
    public KeyManager(@NotNull BiFunction<Map.Entry<String, String>, String, Boolean> keyMatcher, @NotNull Mode mode) {
        super(keyMatcher, mode);
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier the key to register
     * @param value the value to associate with the key
     * @throws RegisteredReferenceException if {@code identifier} is already registered
     */
    @Override
    public final void add(@NotNull String identifier, @NotNull String value) throws RegisteredReferenceException {
        super.add(identifier, value);
    }

    /**
     * Registers a key-value pair if the optional value is present.
     * <p>
     * If the optional is empty, this method is a no-op.
     *
     * @param identifier the key to register
     * @param value an optional value; registration occurs only when present
     * @throws RegisteredReferenceException if {@code identifier} is already registered
     */
    public final void add(@NotNull String identifier, @NotNull Optional<String> value) throws RegisteredReferenceException {
        value.ifPresent(v -> this.add(identifier, v));
    }

    /**
     * Registers a key-value pair extracted from a {@link Pair} whose value is optional.
     * <p>
     * If the pair's value is empty, this method is a no-op. This is convenient for use with
     * environment-variable helpers that return {@code Pair<String, Optional<String>>}.
     *
     * @param pair a pair containing the key and an optional value
     * @throws RegisteredReferenceException if the key is already registered
     * @see dev.simplified.util.SystemUtil#getEnvPair(String)
     */
    public final void add(@NotNull Pair<String, Optional<String>> pair) throws RegisteredReferenceException {
        pair.getValue().ifPresent(value -> this.add(pair.getKey(), value));
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier the key to look up
     * @return the value associated with the key
     * @throws UnknownReferenceException if {@code identifier} is not registered
     */
    @Override
    public final @NotNull String get(@NotNull String identifier) throws UnknownReferenceException {
        return super.get(identifier);
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier the key to look up
     * @return an {@link Optional} containing the value, or empty if not registered
     */
    @Override
    public final @NotNull Optional<String> getOptional(@NotNull String identifier) {
        return super.getOptional(identifier);
    }

    /**
     * Returns a {@link Supplier} that lazily retrieves the optional value for the given key.
     * <p>
     * Each invocation of the supplier performs a fresh lookup, so it reflects the current
     * state of the manager at call time.
     *
     * @param identifier the key to look up
     * @return a supplier producing an {@link Optional} of the current value
     */
    public final @NotNull Supplier<Optional<String>> getSupplier(@NotNull String identifier) {
        return () -> super.getOptional(identifier);
    }

    /**
     * Registers or replaces the value for the given key.
     *
     * @param identifier the key to register or replace
     * @param value the value to associate with the key
     * @throws InsufficientModeException if the current mode does not permit updates
     */
    public final void put(@NotNull String identifier, @NotNull String value) throws InsufficientModeException {
        super.put(identifier, value);
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier the key to remove
     * @throws InsufficientModeException if the current mode does not permit removal
     * @throws UnknownReferenceException if {@code identifier} is not registered
     */
    @Override
    public final void remove(@NotNull String identifier) throws InsufficientModeException, UnknownReferenceException {
        super.remove(identifier);
    }

    /**
     * {@inheritDoc}
     *
     * @param identifier the key whose value should be replaced
     * @param newValue the new value to associate with the key
     * @throws InsufficientModeException if the current mode does not permit updates
     * @throws UnknownReferenceException if {@code identifier} is not registered
     */
    @Override
    public final void update(@NotNull String identifier, @NotNull String newValue) throws InsufficientModeException, UnknownReferenceException {
        super.update(identifier, newValue);
    }

}

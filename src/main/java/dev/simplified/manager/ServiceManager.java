package dev.simplified.manager;

import dev.simplified.manager.exception.InsufficientModeException;
import dev.simplified.manager.exception.RegisteredReferenceException;
import dev.simplified.manager.exception.UnknownReferenceException;
import org.jetbrains.annotations.NotNull;

/**
 * A class-keyed service locator that stores singleton instances indexed by their service type.
 * <p>
 * Keys are {@link Class} objects and values are the corresponding service instances.
 * Lookups use {@link Class#isAssignableFrom(Class)} so that registering an implementation
 * under a supertype or interface allows retrieval by that supertype. This manager is
 * intended to be instantiated once in a central location and shared across the application.
 *
 * <pre>{@code
 * ServiceManager services = new ServiceManager(Mode.UPDATE);
 * services.add(Gson.class, gsonInstance);
 * Gson gson = services.get(Gson.class);
 * }</pre>
 *
 * @see Manager
 */
@SuppressWarnings({ "unchecked" })
public class ServiceManager extends Manager<Class<?>, Object> {

    /**
     * Creates a new {@code ServiceManager} with {@link Mode#NORMAL} mode.
     */
    public ServiceManager() {
        this(Mode.NORMAL);
    }

    /**
     * Creates a new {@code ServiceManager} with the given mode.
     *
     * @param mode the mutability mode controlling which operations are permitted
     * @see Mode
     */
    public ServiceManager(@NotNull Mode mode) {
        super((entry, service) -> service.isAssignableFrom(entry.getKey()), mode);
    }

    /**
     * Registers an instance for the given service class.
     *
     * @param service the service class used as the registry key
     * @param instance the singleton instance to store
     * @param <T> the service type
     * @throws RegisteredReferenceException if {@code service} is already registered
     */
    public final <T> void add(@NotNull Class<T> service, @NotNull T instance) throws RegisteredReferenceException {
        super.add(service, instance);
    }

    /**
     * Retrieves the instance for the given service class.
     *
     * @param service the service class to look up
     * @param <T> the service type
     * @return the registered instance cast to {@code T}
     * @throws UnknownReferenceException if {@code service} is not registered
     * @see #isRegistered 
     */
    public final <T> @NotNull T get(@NotNull Class<T> service) throws UnknownReferenceException {
        return (T) super.get(service);
    }

    /**
     * Registers or replaces the instance for the given service class.
     *
     * @param service the service class used as the registry key
     * @param instance the singleton instance to store
     * @param <T> the service type
     * @throws InsufficientModeException if the current mode does not permit updates
     */
    public final <T> void put(@NotNull Class<T> service, @NotNull T instance) throws InsufficientModeException {
        super.put(service, instance);
    }

    /**
     * Removes the instance for the given service class.
     *
     * @param service the service class to remove
     * @param <T> the service type
     * @throws UnknownReferenceException if {@code service} is not registered
     * @throws InsufficientModeException if the current mode does not permit removal
     */
    public final <T> void remove(@NotNull Class<T> service) throws InsufficientModeException, UnknownReferenceException {
        super.remove(service);
    }

    /**
     * Replaces the instance for the given service class with a new one.
     *
     * @param service the service class whose instance should be replaced
     * @param instance the new instance to associate with the service class
     * @param <T> the service type
     * @throws UnknownReferenceException if {@code service} is not registered
     * @throws InsufficientModeException if the current mode does not permit updates
     */
    public final <T> void update(@NotNull Class<T> service, @NotNull T instance) throws UnknownReferenceException {
        super.update(service, instance);
    }

}

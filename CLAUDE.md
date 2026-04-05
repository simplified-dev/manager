# Manager

Generic resource manager library for mode-controlled, key-based object management.

## Package Structure
- `dev.simplified.manager` - 3 classes (Manager, KeyManager, ServiceManager)
- `dev.simplified.manager.exception` - 4 exception classes

## Key Classes
- `Manager<K, V>` - Abstract base, `ConcurrentMap`-backed, `Mode` enum (NORMAL/UPDATE/ALL), core CRUD
- `KeyManager` - String-keyed manager for config entries, `Optional`/`Supplier` support
- `ServiceManager` - Class-keyed service locator, `isAssignableFrom` lookup, generic `<T>` methods
- `ManagerException` - Base unchecked exception
- `InsufficientModeException` - Mode level too low for operation
- `RegisteredReferenceException` - Duplicate key on add
- `UnknownReferenceException` - Key not found on get/update/remove

## Dependencies
- `com.github.simplified-dev:collections:master-SNAPSHOT` (ConcurrentMap, Pair)
- `org.jetbrains:annotations` (@NotNull)
- `org.projectlombok:lombok` (compile-only: @Getter, @RequiredArgsConstructor)

## Build
```bash
./gradlew build
./gradlew test
```

## Java Version
- Java 21 (toolchain enforced)

## Key Patterns
- `Manager` uses `BiFunction<Map.Entry<K, V>, K, Boolean>` for key matching
- Mode levels: NORMAL(0) < UPDATE(1) < ALL(2)
- `KeyManager` accepts `Pair<String, Optional<String>>` for env-var helpers
- `ServiceManager` uses `Class.isAssignableFrom` for interface/supertype matching
- No tests in this module

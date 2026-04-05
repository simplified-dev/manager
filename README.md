# Manager

A generic resource manager library for Java 21 providing mode-controlled, key-based object management. Includes a base `Manager` class with operational modes (NORMAL, UPDATE, ALL), a `KeyManager` for string-keyed configuration entries, and a `ServiceManager` for class-keyed service location - all backed by thread-safe concurrent collections.

> [!IMPORTANT]
> This library requires **Java 21+** and depends on the [collections](https://github.com/Simplified-Dev/collections) module for thread-safe data structures.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Usage](#usage)
- [Architecture](#architecture)
  - [Mode System](#mode-system)
  - [Exception Hierarchy](#exception-hierarchy)
- [API Overview](#api-overview)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Mode-controlled mutations** - Three operational modes (NORMAL, UPDATE, ALL) control which write operations are permitted, enforced at runtime with typed exceptions
- **Key-based management** - `KeyManager` provides string-keyed registration, retrieval, update, and removal of configuration values
- **Service location** - `ServiceManager` stores singleton instances indexed by their `Class` type, with `isAssignableFrom` lookup for interface/supertype retrieval
- **Thread-safe** - All internal storage uses `ConcurrentMap` from the Simplified collections library
- **Type-safe exceptions** - Dedicated exceptions for duplicate registration, unknown references, and insufficient mode permissions
- **Optional-friendly** - Lookup methods return `Optional` for safe value retrieval alongside strict `get` variants that throw on missing entries

## Getting Started

### Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| [Java](https://adoptium.net/) | **21+** | Required |
| [Gradle](https://gradle.org/) | **9.4+** | Build tool (wrapper included) |
| [Git](https://git-scm.com/) | 2.x+ | For cloning the repository |

### Installation

<details>
<summary>Gradle (Kotlin DSL)</summary>

Add the JitPack repository and dependency to your `build.gradle.kts`:

```kotlin
repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.simplified-dev:manager:master-SNAPSHOT")
}
```

</details>

<details>
<summary>Gradle (Groovy DSL)</summary>

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.simplified-dev:manager:master-SNAPSHOT'
}
```

</details>

<details>
<summary>Maven</summary>

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.simplified-dev</groupId>
    <artifactId>manager</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

</details>

### Usage

#### KeyManager - String-Keyed Configuration

```java
// Create a key manager with case-insensitive matching and UPDATE mode
KeyManager keys = new KeyManager(
    (entry, key) -> key.equalsIgnoreCase(entry.getKey()),
    Manager.Mode.UPDATE
);

// Register entries
keys.add("API_KEY", "sk-abc123");
keys.add("DATABASE_URL", "jdbc:postgresql://localhost/mydb");

// Retrieve values
String apiKey = keys.get("API_KEY");                    // throws if missing
Optional<String> optional = keys.getOptional("API_KEY"); // safe retrieval

// Update (requires Mode.UPDATE or Mode.ALL)
keys.update("API_KEY", "sk-new456");

// Put (add or replace)
keys.put("API_KEY", "sk-replaced789");
```

#### ServiceManager - Class-Keyed Service Locator

```java
// Create a service manager with ALL mode (add, update, remove)
ServiceManager services = new ServiceManager(Manager.Mode.ALL);

// Register service instances
services.add(Gson.class, new GsonBuilder().setPrettyPrinting().create());
services.add(HttpClient.class, HttpClient.newHttpClient());

// Retrieve by class type (uses isAssignableFrom for interface matching)
Gson gson = services.get(Gson.class);

// Replace an existing service
services.update(Gson.class, new Gson());

// Remove a service
services.remove(HttpClient.class);
```

#### Custom Manager

```java
// Extend Manager<K, V> for custom key and value types
public class PlayerManager extends Manager<UUID, Player> {
    public PlayerManager() {
        super((entry, key) -> entry.getKey().equals(key), Mode.ALL);
    }

    // Expose protected methods as public API
    public void register(UUID id, Player player) { add(id, player); }
    public Player find(UUID id) { return get(id); }
    public void unregister(UUID id) { remove(id); }
}
```

## Architecture

### Mode System

The `Manager.Mode` enum controls which mutating operations are permitted on a manager instance:

| Mode | Level | Add | Update | Remove | Clear |
|------|-------|-----|--------|--------|-------|
| `NORMAL` | 0 | Yes | No | No | No |
| `UPDATE` | 1 | Yes | Yes | No | No |
| `ALL` | 2 | Yes | Yes | Yes | Yes |

Operations that exceed the current mode throw `InsufficientModeException`.

> [!TIP]
> Choose the most restrictive mode that satisfies your use case. `NORMAL` is safest for write-once registries; `ALL` is appropriate for fully dynamic managers.

### Exception Hierarchy

```
ManagerException (base)
├── InsufficientModeException   - Operation not permitted by current Mode
├── RegisteredReferenceException - Key already registered (duplicate add)
└── UnknownReferenceException   - Key not found (get/update/remove on missing entry)
```

All exceptions extend `ManagerException`, which extends `RuntimeException` (unchecked).

## API Overview

### Manager (Abstract Base)

| Method | Mode Required | Description |
|--------|--------------|-------------|
| `add(K, V)` | Any | Register a new key-value pair (throws if duplicate) |
| `get(K)` | Any | Retrieve value or throw `UnknownReferenceException` |
| `getOptional(K)` | Any | Retrieve value as `Optional` |
| `isRegistered(K)` | Any | Check if a key exists |
| `put(K, V)` | UPDATE+ | Add or replace a key-value pair |
| `update(K, V)` | UPDATE+ | Replace value for existing key (throws if missing) |
| `remove(K)` | ALL | Remove a key-value pair (throws if missing) |
| `clear()` | ALL | Remove all entries |

### KeyManager

String-keyed manager exposing all `Manager` operations as public methods, plus:

| Method | Description |
|--------|-------------|
| `add(String, Optional<String>)` | Register only if the optional is present |
| `add(Pair<String, Optional<String>>)` | Register from a key-optional pair |
| `getSupplier(String)` | Returns a `Supplier` that lazily performs a fresh lookup |

### ServiceManager

Class-keyed manager using `isAssignableFrom` for type-safe lookups. All methods are generic (`<T>`) and cast-safe.

## Project Structure

```
manager/
├── src/main/java/dev/simplified/manager/
│   ├── Manager.java                # Abstract base - ConcurrentMap, Mode enum, core CRUD
│   ├── KeyManager.java             # String-keyed manager for config/API keys
│   ├── ServiceManager.java         # Class-keyed service locator
│   └── exception/
│       ├── ManagerException.java           # Base runtime exception
│       ├── InsufficientModeException.java  # Mode violation
│       ├── RegisteredReferenceException.java # Duplicate key
│       └── UnknownReferenceException.java  # Missing key
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
│   ├── libs.versions.toml          # Version catalog
│   └── wrapper/
└── LICENSE.md
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for development setup, code style guidelines, and how to submit a pull request.

## License

This project is licensed under the **Apache License 2.0** - see [LICENSE.md](LICENSE.md) for the full text.

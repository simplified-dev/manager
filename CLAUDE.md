# Manager

Generic resource manager library for mode-controlled, key-based object management.

## Key Patterns
- `Mode` levels are hierarchical, not flags: NORMAL(0) < UPDATE(1) < ALL(2). An operation requiring a higher level than the manager's current mode throws `InsufficientModeException`.
- `ServiceManager` lookup matches on `Class.isAssignableFrom`, so a request for an interface or supertype resolves a registered subtype.

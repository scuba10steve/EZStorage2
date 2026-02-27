# Configuration System

Steve's Simple Storage uses NeoForge's ModConfigSpec system to generate TOML configuration files. The configuration is automatically created at `config/s3-common.toml` when the mod first loads.

## Configuration File Structure

The configuration is organized into four main sections:

### Capacities Section

Controls the storage capacity for each tier of storage block:

```toml
[capacities]
    basicCapacity = 10000        # Storage Box
    condensedCapacity = 40000    # Condensed Storage Box
    compressedCapacity = 80000   # Compressed Storage Box
    superCapacity = 160000       # Super Storage Box
    ultraCapacity = 640000       # Ultra Storage Box
    hyperCapacity = 2560000      # Hyper Storage Box
    ultimateCapacity = 10240000  # Ultimate Storage Box
```

**Notes:**
- All capacity values must be greater than 1
- Values are applied at runtime when blocks are scanned
- Changes require a game restart

### Features Section

Toggles for optional mod features:

```toml
[features]
    enableSecurity = true        # Security Box and Key item
    enableTerminal = true        # Access Terminal block
    enableDolly = true          # Dolly items for moving blocks
    enableSearchModes = true    # Advanced search ($oredict, @mod, %tab)
    enableOpOverride = true     # Allow ops to bypass security
```

**Notes:**
- All feature toggles are actively used
- Security Box, Access Terminal, Dolly, and Search Modes are all implemented

### Recipes Section

Controls recipe difficulty:

```toml
[recipes]
    classicRecipes = false      # Use easier classic recipes
    toughHyper = false         # Make Hyper Storage Box more expensive
```

**Notes:**
- Recipe options control which recipe set is used for crafting
- Changes require a game restart to take effect

### Integration Section

Controls mod integration features:

```toml
[integration]
    jeiIntegration = true       # Enable JEI integration
```

**Notes:**
- JEI integration is currently active
- Future integrations will be added here

## Implementation Details

### Config Loading

The configuration is loaded during mod initialization in the NeoForge module:

```java
// neoforge/.../StevesSimpleStorage.java
modContainer.registerConfig(ModConfig.Type.COMMON, StorageConfig.SPEC);
```

### Accessing Config Values

The `common` module accesses config values through the `S3Config` platform abstraction interface:

```java
// common/.../platform/S3Config.java — interface
int capacity = S3Platform.getConfig().getBasicCapacity();
boolean enabled = S3Platform.getConfig().isSecurityEnabled();
```

The NeoForge module provides the implementation (`NeoForgeConfig`) that delegates to `StorageConfig`'s `ModConfigSpec` values.

### Runtime Application

Storage capacities are applied dynamically when blocks are scanned. Each `BlockStorage` instance holds a tier key string and looks up its capacity via the platform config:

```java
public int getCapacity() {
    return S3Platform.getConfig().getCapacityForTier(this.tierKey);
}
```

This allows config changes to take effect on restart without requiring block re-registration.

## Future Plans

### GUI Configuration Editor

A planned feature will allow editing configuration values in-game through a GUI:

- Access via mod menu or command
- Live preview of changes
- Validation of input values
- Save and reload without full restart (where possible)

### Per-World Configuration

Future versions may support per-world configuration overrides:

- World-specific capacity limits
- Feature toggles per save
- Server-side enforcement

## Troubleshooting

### Config File Not Generated

If the config file doesn't appear:
1. Check `logs/latest.log` for errors
2. Ensure `config/` directory exists
3. Verify mod loaded successfully

### Config Changes Not Applied

If changes don't take effect:
1. Ensure you saved the TOML file
2. Restart the game completely
3. Check for syntax errors in TOML
4. Verify values are within valid ranges

### Invalid Config Values

If the config file is corrected on load:
- Check logs for "Configuration file is not correct" message
- Invalid values are reset to defaults
- Ensure numeric values are within specified ranges

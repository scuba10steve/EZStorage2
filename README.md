# Steve's Simple Storage (S3)

[![CI](https://github.com/scuba10steve/steves-simple-storage/actions/workflows/ci.yml/badge.svg)](https://github.com/scuba10steve/steves-simple-storage/actions/workflows/ci.yml)
[![Game Tests](https://github.com/scuba10steve/steves-simple-storage/actions/workflows/game-tests.yml/badge.svg)](https://github.com/scuba10steve/steves-simple-storage/actions/workflows/game-tests.yml)
[![Modrinth](https://img.shields.io/modrinth/dt/XsvAJLLz?logo=modrinth&label=Modrinth)](https://modrinth.com/mod/XsvAJLLz)
[![CurseForge](https://img.shields.io/curseforge/dt/1469363?logo=curseforge&label=CurseForge)](https://www.curseforge.com/minecraft/mc-mods/steves-simple-storage)

Steve's Simple Storage introduces a storage system that scales and evolves as players progress. Want to put 100,000 Cobblestone in 1 slot? No problem. Blocks in the mod can add a search bar, a crafting grid, additional storage, external access, security, and more. Also includes JEI integration for 1-click crafting from the system's internal inventory!

## Project Status

Version 0.5.2 — fully ported to **Minecraft 1.21.1** / **NeoForge 21.1.218**. Core storage functionality is complete and stable. See [docs/port-overview.md](docs/port-overview.md) for details.

## Requirements

- **Java 21** or higher (required for NeoForge)
- **Gradle 8.x** (wrapper included)

## Building the Mod

1. **Clone the repository**:
   ```bash
   git clone https://github.com/scuba10steve/steves-simple-storage.git
   cd steves-simple-storage
   ```

2. **Build the mod**:
   ```bash
   ./gradlew :neoforge:build
   ```

   On Windows:
   ```cmd
   gradlew.bat :neoforge:build
   ```

3. **Find the built mod**:
   The compiled mod JAR will be located at `neoforge/build/libs/s3-<version>.jar`

## Development

### Setting up the Development Environment

1. **Import the project** into your IDE (IntelliJ IDEA or Eclipse recommended)
2. **Run the client** for testing:
   ```bash
   ./gradlew :neoforge:runClient
   ```
3. **Run the server** for testing:
   ```bash
   ./gradlew :neoforge:runServer
   ```

### Running Tests

```bash
# Run common module unit tests
./gradlew :common:test

# Run full build (both modules)
./gradlew build

# View test report
open common/build/reports/tests/test/index.html
```

### Project Structure

This project uses a multi-module Gradle layout for cleaner architecture and future multi-loader readiness:

```
steves-simple-storage/
├── common/                  # Platform-agnostic code (vanilla MC only)
│   └── src/
│       ├── main/java/       # Blocks, block entities, menus, screens, packets, storage logic
│       ├── main/resources/  # Assets (textures, models, lang) and data (recipes, loot, tags)
│       └── test/java/       # Unit tests
├── neoforge/                # NeoForge-specific code
│   └── src/
│       ├── main/java/       # Mod entry point, registration, config, packet handlers, JEI
│       ├── main/resources/  # neoforge.mods.toml
│       └── generated/       # Datagen output
├── build.gradle             # Root: shared subproject config
├── settings.gradle          # Includes common and neoforge modules
└── gradle.properties        # Mod version and dependency versions
```

### Key Files

- **Mod entry point**: `neoforge/.../StevesSimpleStorage.java`
- **Registration**: `neoforge/.../init/` (ModBlocks, ModItems, ModBlockEntities, ModMenuTypes)
- **Platform abstraction**: `common/.../platform/` (S3Platform, S3Config, S3NetworkHelper)
- **Blocks**: `common/.../block/` (most blocks) and `neoforge/.../block/` (port blocks)
- **Block Entities**: `common/.../blockentity/` (most) and `neoforge/.../blockentity/` (port BEs)
- **Storage logic**: `common/.../storage/` (StorageInventory, StoredItemStack)
- **Mod metadata**: `neoforge/src/main/resources/META-INF/neoforge.mods.toml`

## Configuration

Steve's Simple Storage uses TOML configuration files located at `config/s3-common.toml`. The configuration is organized into sections:

### Storage Capacities
- `basicCapacity` - Storage Box capacity (default: 10,000)
- `condensedCapacity` - Condensed Storage Box capacity (default: 40,000)
- `compressedCapacity` - Compressed Storage Box capacity (default: 80,000)
- `superCapacity` - Super Storage Box capacity (default: 160,000)
- `ultraCapacity` - Ultra Storage Box capacity (default: 640,000)
- `hyperCapacity` - Hyper Storage Box capacity (default: 2,560,000)
- `ultimateCapacity` - Ultimate Storage Box capacity (default: 10,240,000)

### Feature Toggles
- `enableSecurity` - Enable Security Box and Key item (default: true)
- `enableTerminal` - Enable Access Terminal block (default: true)
- `enableDolly` - Enable Dolly items for moving blocks (default: true)
- `enableSearchModes` - Enable advanced search modes (default: true)
- `enableOpOverride` - Allow operators to override security (default: true)

### Recipe Options
- `classicRecipes` - Use classic (easier) recipes (default: false)
- `toughHyper` - Make Hyper Storage Box recipe more expensive (default: false)

### Mod Integration
- `jeiIntegration` - Enable JEI integration features (default: true)

### Automation Settings
- `extractPortInterval` - Ticks between Extract Port extraction attempts (default: 8, range: 1-100). Lower values = faster extraction, higher values = less server load.
- `minSyncInterval` - Minimum ticks between storage inventory sync packets (default: 2, range: 0-20). Helps prevent visual flicker. Set to 0 to disable throttling.

Configuration changes require a game restart to take effect.

## License

This Open Source project is licensed under the MIT License (see [LICENSE](LICENSE)).

## Credits

- **Current Maintainer**: scuba10steve
- **Original Authors**: zerofall, SBlectric
- **Game**: [Minecraft](http://www.minecraft.net/) by [Mojang AB](http://mojang.com/)
- **Mod Loader**: [NeoForge](https://neoforged.net/)

## Links

- [GitHub Repository](https://github.com/scuba10steve/steves-simple-storage)
- [Original Project on CurseForge](https://www.curseforge.com/minecraft/mc-mods/ezstorage-2) (EZStorage 2 - 1.12.2 version)

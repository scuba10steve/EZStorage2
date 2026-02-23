# Steve's Simple Storage (S3)

Steve's Simple Storage introduces a storage system that scales and evolves as players progress. Want to put 100,000 Cobblestone in 1 slot? No problem. Blocks in the mod can add a search bar, a crafting grid, additional storage, external access, security, and more. Also includes JEI integration for 1-click crafting from the system's internal inventory!

## Project Status

Version 0.1.0-beta — fully ported to **Minecraft 1.21.1** / **NeoForge 21.1.218**. Core storage functionality is complete and stable. See [docs/port-overview.md](docs/port-overview.md) for details and [docs/future-ideas.md](docs/future-ideas.md) for planned features.

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
   ./gradlew build
   ```
   
   On Windows:
   ```cmd
   gradlew.bat build
   ```

3. **Find the built mod**:
   The compiled mod JAR will be located at `build/libs/s3-0.1.0.jar`

## Development

### Setting up the Development Environment

1. **Import the project** into your IDE (IntelliJ IDEA or Eclipse recommended)
2. **Run the client** for testing:
   ```bash
   ./gradlew runClient
   ```
3. **Run the server** for testing:
   ```bash
   ./gradlew runServer
   ```

### Running Tests

```bash
# Run unit tests
./gradlew test

# View test report
open build/reports/tests/test/index.html
```

### Project Structure

- `src/main/java/` - Current NeoForge implementation
- `src/main/resources/` - Mod resources and metadata
- `src/test/java/` - Unit tests
- `build.gradle` - Build configuration using ModDevGradle
- `gradle.properties` - Mod properties and versions

### Key Files

- **Main mod class**: `src/main/java/io/github/scuba10steve/s3/StevesSimpleStorage.java`
- **Registration**: `src/main/java/io/github/scuba10steve/s3/init/`
- **Blocks**: `src/main/java/io/github/scuba10steve/s3/block/`
- **Items**: `src/main/java/io/github/scuba10steve/s3/item/`
- **Block Entities**: `src/main/java/io/github/scuba10steve/s3/blockentity/`
- **Mod metadata**: `src/main/resources/META-INF/neoforge.mods.toml`

## Configuration

Steve's Simple Storage uses TOML configuration files located at `config/s3-common.toml`. The configuration is organized into sections:

### Storage Capacities
- `basicCapacity` - Storage Box capacity (default: 10,000)
- `condensedCapacity` - Condensed Storage Box capacity (default: 40,000)
- `compressedCapacity` - Compressed Storage Box capacity (default: 80,000)
- `superCapacity` - Super Storage Box capacity (default: 160,000)
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

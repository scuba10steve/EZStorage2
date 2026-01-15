# EZStorage 2

EZStorage 2 introduces a storage system that scales and evolves as players progress. Want to put 100,000 Cobblestone in 1 slot? No problem. Blocks in the mod can add a search bar, a crafting grid, additional storage, external access, security, and more. Also includes JEI integration for 1-click crafting from the system's internal inventory!

## Project Status

This repository contains EZStorage 2 updated for **Minecraft 1.21.1** and **NeoForge 21.1.218**. 

✅ **Current Status**: Version 0.1.0-beta - Core storage functionality complete and stable. Advanced features planned for future releases.

### What's Working
- ✅ Builds successfully with Gradle 8.10.2
- ✅ Compatible with Minecraft 1.21.1
- ✅ Uses NeoForge 21.1.218 with ModDevGradle 2.0.139 (stable)
- ✅ Modern registration system with DeferredRegister
- ✅ Basic blocks: Storage Box, Storage Core
- ✅ Tiered storage blocks: Condensed, Super, Ultra, Hyper Storage Boxes (all textures working)
- ✅ Basic items: Key, Dolly (basic & super), Block Items
- ✅ Block entities: Storage Core Block Entity with full functionality
- ✅ GUI system: Storage Core menu and screen with proper item display and interaction
- ✅ Storage system: Inventory with insert/extract logic, item display, scrolling
- ✅ Networking: Storage sync and click packets for client-server communication
- ✅ JEI integration: Recipe transfer and storage item lookup (updated to 19.27.0.336)
- ✅ Multiblock structures: Core scanning and validation system with dynamic capacity
- ✅ Assets: Recipes, language files, loot tables, and block tags
- ✅ Unit tests for core constants
- ✅ Mod loads and runs successfully in development and production environments
- ✅ All UI sync issues resolved
- ✅ Click interactions working correctly (left-click, right-click, shift-click, drag-and-drop)
- ✅ Capacity updates dynamically when blocks are added/removed

### Not Yet Ported
- ❌ Crafting Box (crafting grid integration)
- ❌ Search Box (item filtering and search)
- ❌ Security Box (player access control)
- ❌ Sort Box (automatic sorting modes)
- ❌ Access Terminal (remote storage access)
- ❌ Input Port (item input automation)
- ❌ Extract Port (item extraction automation)
- ❌ Eject Port (item ejection)
- ❌ Blank Box (decorative multiblock component)
- ❌ Crafting GUI integration
- ❌ Search and filtering system
- ❌ Security system with player permissions
- ❌ Sorting modes (alphabetical, quantity, mod)
- ❌ Extract list filtering (whitelist/blacklist)

### Port Complete! 🎉
EZStorage 2 core functionality has been successfully ported from Minecraft 1.12.2 to 1.21.1. The basic storage system is fully functional with item storage, retrieval, and multiblock capacity scaling. Advanced features listed above are planned for future updates.

The original 1.12.2 source code has been preserved in `src/main/java-backup/` for reference during the porting process.

## Requirements

- **Java 21** or higher (required for NeoForge)
- **Gradle 8.x** (wrapper included)

## Building the Mod

1. **Clone the repository**:
   ```bash
   git clone https://github.com/zerofall/EZStorage2.git
   cd EZStorage2
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
   The compiled mod JAR will be located at `build/libs/ezstorage-2.5.0.jar`

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
- `src/main/java-backup/` - Original 1.12.2 source code for reference
- `src/main/resources/` - Mod resources and metadata
- `src/test/java/` - Unit tests
- `build.gradle` - Build configuration using ModDevGradle
- `gradle.properties` - Mod properties and versions

### Key Files

- **Main mod class**: `src/main/java/com/zerofall/ezstorage/EZStorage.java`
- **Registration**: `src/main/java/com/zerofall/ezstorage/init/`
- **Blocks**: `src/main/java/com/zerofall/ezstorage/block/`
- **Items**: `src/main/java/com/zerofall/ezstorage/item/`
- **Block Entities**: `src/main/java/com/zerofall/ezstorage/blockentity/`
- **Mod metadata**: `src/main/resources/META-INF/neoforge.mods.toml`

## Contributing

This mod is in active development for the 1.21.1 update. The registration foundation is complete and ready for feature implementation. Key areas that need work:

1. **Storage Logic** - Port storage system from 1.12.2 codebase
2. **GUI System** - Port all user interfaces to modern Minecraft
3. **Networking** - Update packet system for NeoForge
4. **Assets** - Create models, textures, and blockstates
5. **JEI Integration** - Restore JEI compatibility
6. **Multiblock Logic** - Port storage multiblock system

## Configuration

EZStorage 2 uses TOML configuration files located at `config/ezstorage-common.toml`. The configuration is organized into sections:

### Storage Capacities
- `basicCapacity` - Storage Box capacity (default: 10,000)
- `condensedCapacity` - Condensed Storage Box capacity (default: 40,000)
- `superCapacity` - Super Storage Box capacity (default: 160,000)
- `ultraCapacity` - Ultra Storage Box capacity (default: 640,000)
- `hyperCapacity` - Hyper Storage Box capacity (default: 2,560,000)

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

Configuration changes require a game restart to take effect.

## License

This Open Source project is licensed under the MIT License (see [LICENSE](LICENSE)).

## Credits

- **Current Maintainer**: scuba10steve
- **Original Authors**: zerofall, SBlectric
- **Game**: [Minecraft](http://www.minecraft.net/) by [Mojang AB](http://mojang.com/)
- **Mod Loader**: [NeoForge](https://neoforged.net/)

## Links

- [GitHub Repository](https://github.com/scuba10steve/EZStorage2)
- [Original Project on CurseForge](http://minecraft.curseforge.com/projects/ezstorage-2) (1.12.2 version)

# EZStorage 2

EZStorage 2 introduces a storage system that scales and evolves as players progress. Want to put 100,000 Cobblestone in 1 slot? No problem. Blocks in the mod can add a search bar, a crafting grid, additional storage, external access, security, and more. Also includes JEI integration for 1-click crafting from the system's internal inventory!

## Project Status

This repository contains EZStorage 2 updated for **Minecraft 1.21.1** and **NeoForge 21.1.77**. 

✅ **Current Status**: The mod has been successfully updated to modern NeoForge with a working registration system and basic block/item/entity structure.

### What's Working
- ✅ Builds successfully with Gradle 8.10.2
- ✅ Compatible with Minecraft 1.21.1
- ✅ Uses NeoForge 21.1.77 with ModDevGradle 2.0.46-beta
- ✅ Modern registration system with DeferredRegister
- ✅ Basic blocks: Storage Box, Storage Core
- ✅ Basic items: Key, Dolly (basic & super), Block Items
- ✅ Block entities: Storage Core Block Entity
- ✅ GUI system: Storage Core menu and screen
- ✅ Storage system: Basic inventory with insert/extract logic
- ✅ Networking: Storage sync packets for client-server communication
- ✅ Unit tests for core constants
- ✅ Mod loads successfully in development environment

### What Needs Work
- ❌ JEI integration
- ❌ Multiblock structures and validation
- ❌ Assets (models, textures, blockstates, recipes)

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

## License

This Open Source project is licensed under the MIT License (see [LICENSE](LICENSE)).

## Credits

- **Original Authors**: zerofall, SBlectric
- **Game**: [Minecraft](http://www.minecraft.net/) by [Mojang AB](http://mojang.com/)
- **Mod Loader**: [NeoForge](https://neoforged.net/)

## Links

- [Project on CurseForge](http://minecraft.curseforge.com/projects/ezstorage-2) (1.12.2 version)
- [GitHub Repository](https://github.com/zerofall/EZStorage2)

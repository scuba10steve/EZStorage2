# Build System Configuration

## Overview

Steve's Simple Storage uses a **multi-module Gradle project** with **ModDevGradle 2.0.139**. The project is split into two modules:

- **common** - Platform-agnostic code compiled against vanilla Minecraft (via `neoFormVersion`)
- **neoforge** - NeoForge-specific code (registration, config, packet handlers, JEI integration)

## Requirements

### System Requirements
- **Java**: 21 or higher (required for NeoForge 1.21.1)
- **Gradle**: 8.10.2 (provided by wrapper)
- **Memory**: 4GB+ RAM recommended for development
- **Storage**: 2GB+ free space for dependencies and build artifacts

### Development Environment
- **IDE**: IntelliJ IDEA (recommended) or Eclipse
- **Git**: For version control
- **Internet**: For dependency downloads

## Project Structure

```
steves-simple-storage/
├── build.gradle                 # Root: shared subproject config (Java 21, repos)
├── settings.gradle              # Includes common and neoforge modules
├── gradle.properties            # Mod version and dependency versions
├── gradlew / gradlew.bat        # Gradle wrapper
├── common/
│   ├── build.gradle             # neoFormVersion (vanilla MC only), JUnit
│   └── src/
│       ├── main/java/           # Platform-agnostic mod code
│       ├── main/resources/      # Assets and data (textures, models, recipes, etc.)
│       └── test/java/           # Unit tests
├── neoforge/
│   ├── build.gradle             # Full NeoForge, depends on :common
│   └── src/
│       ├── main/java/           # NeoForge-specific code
│       ├── main/resources/      # neoforge.mods.toml, log4j2.xml
│       └── generated/resources/ # Datagen output
└── docs/                        # Documentation
```

## Build Configuration

### Root build.gradle

Shared config applied to both subprojects:

```gradle
subprojects {
    apply plugin: 'java'

    version = rootProject.mod_version
    group = 'io.github.scuba10steve.s3'

    java.toolchain.languageVersion = JavaLanguageVersion.of(21)

    repositories {
        mavenCentral()
        maven { name = 'ProgrammingLife'; url = 'https://maven.blamejared.com' }
    }
}
```

### common/build.gradle

Uses `neoFormVersion` for vanilla Minecraft classes only (no NeoForge APIs):

```gradle
plugins {
    id 'net.neoforged.moddev' version '2.0.139'
}

base { archivesName = 's3-common' }

neoForge {
    neoFormVersion = "${minecraft_version}-20240808.144430"
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.4'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

test { useJUnitPlatform() }
```

### neoforge/build.gradle

Full NeoForge with all run configs, depends on `:common`:

```gradle
plugins {
    id 'net.neoforged.moddev' version '2.0.139'
}

base { archivesName = 's3' }

neoForge {
    version = neoforge_version

    runs {
        client { client() }
        server { server() }
        gameTestServer {
            type = "gameTestServer"
            systemProperty 'neoforge.enabledGameTestNamespaces', 's3'
        }
        data {
            data()
            programArguments.addAll(
                '--mod', 's3', '--all',
                '--output', file('src/generated/resources/').getAbsolutePath(),
                '--existing', file('src/main/resources/').getAbsolutePath(),
                '--existing', rootProject.file('common/src/main/resources/').getAbsolutePath()
            )
        }
    }

    mods {
        s3 {
            sourceSet sourceSets.main
            sourceSet project(':common').sourceSets.main
        }
    }
}

dependencies {
    implementation project(':common')

    compileOnly "mezz.jei:jei-1.21.1-common-api:19.27.0.336"
    compileOnly "mezz.jei:jei-1.21.1-neoforge-api:19.27.0.336"
    runtimeOnly "mezz.jei:jei-1.21.1-neoforge:19.27.0.336"

    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.4'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

test { useJUnitPlatform() }
```

**Key detail**: The `mods` block must include both `sourceSets.main` and `project(':common').sourceSets.main` so NeoForge discovers classes from both modules as part of the `s3` mod.

### gradle.properties

```properties
minecraft_version=1.21.1
neoforge_version=21.1.218
mod_version=0.3.1
mod_id=s3
mod_name=Steve's Simple Storage
mod_license=MIT
mod_authors=zerofall, SBlectric
mod_description=Massive, scalable storage for Minecraft.
```

### settings.gradle

```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { name = 'NeoForged'; url = 'https://maven.neoforged.net/releases' }
    }
}

plugins {
    id 'org.gradle.toolchains.foojay-resolver-convention' version '0.8.0'
}

rootProject.name = 'steves-simple-storage'
include 'common'
include 'neoforge'
```

## Gradle Tasks

### Common Tasks

```bash
# Build the mod (neoforge module produces the installable JAR)
./gradlew :neoforge:build

# Run common module unit tests
./gradlew :common:test

# Build everything
./gradlew build

# Clean build artifacts
./gradlew clean

# Run development client
./gradlew :neoforge:runClient

# Run development server
./gradlew :neoforge:runServer

# Run game tests
./gradlew :neoforge:runGameTestServer

# Run datagen
./gradlew :neoforge:runData
```

### Advanced Tasks

```bash
# Build with full clean
./gradlew clean build

# Run tests with detailed output
./gradlew :common:test --info

# Build without daemon (CI/CD)
./gradlew build --no-daemon

# Refresh dependencies
./gradlew build --refresh-dependencies

# Compile only (no JAR)
./gradlew :common:compileJava :neoforge:compileJava
```

## Platform Abstraction

The multi-module split uses a platform abstraction layer to decouple common code from NeoForge APIs:

- **`S3Platform`** - Central static holder for all platform-specific references
- **`S3Config`** - Interface for config values (implemented by `NeoForgeConfig`)
- **`S3NetworkHelper`** - Interface for sending packets (implemented by `NeoForgeNetworkHelper`)
- **`S3Platform.MenuOpener`** - Abstraction for `ServerPlayer.openMenu(provider, pos)`

All platform holders are initialized in `StevesSimpleStorage` (the NeoForge `@Mod` entry point) during mod construction.

## Module Boundaries

### What goes in `common`
- Block classes (except port blocks that create NeoForge-specific BEs)
- Block entities (except port BEs that use `IItemHandler`)
- Menus and screens (except ExtractPort which is coupled to its neoforge BE)
- Packet record definitions (TYPE + STREAM_CODEC)
- Storage logic, utilities, enums
- Assets and data resources

### What goes in `neoforge`
- `@Mod` entry point (`StevesSimpleStorage`)
- `DeferredRegister` registration classes (`ModBlocks`, `ModItems`, etc.)
- `StorageConfig` (uses `ModConfigSpec`)
- Packet handler methods (use `IPayloadContext`)
- `ModNetwork` packet registration
- Port block entities (`IItemHandler`)
- Port blocks and extract port menu/screen
- JEI integration, datagen, game tests
- Event handlers (`SecurityEvents`, `ClientEvents`)

## Build Outputs

### Generated Artifacts

**Main Jar** (installable):
- Location: `neoforge/build/libs/s3-<version>.jar`
- Contains: Compiled mod classes from both modules and resources

**Common Jar** (not installable directly):
- Location: `common/build/libs/s3-common-<version>.jar`
- Contains: Platform-agnostic classes only

## Continuous Integration

### GitHub Actions Example

```yaml
name: Build
on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-java@v4
      with:
        java-version: '21'
        distribution: 'temurin'
    - name: Build with Gradle
      run: ./gradlew build --no-daemon
    - name: Upload artifacts
      uses: actions/upload-artifact@v4
      with:
        name: mod-jar
        path: neoforge/build/libs/*.jar
```

## Troubleshooting

### Common Build Issues

**"Cannot find symbol" in common module**: Ensure you're not importing `net.neoforged` packages. Common module only has vanilla MC classes.

**"project(':common').sourceSets not found"**: Make sure `settings.gradle` includes both `common` and `neoforge`.

**Datagen can't find common resources**: The `--existing` argument in the data run config must point to `rootProject.file('common/src/main/resources/')`.

**Dependency Resolution**:
```bash
./gradlew build --refresh-dependencies
```

**Clean Build**:
```bash
./gradlew clean build
```

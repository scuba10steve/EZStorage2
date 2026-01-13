# Build System Configuration

## Overview

EZStorage 2 uses **ModDevGradle 2.0.46-beta** as its build system, which is the modern replacement for ForgeGradle. This document covers the complete build configuration and development setup.

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
EZStorage2/
├── build.gradle                 # Main build configuration
├── settings.gradle              # Project settings
├── gradle.properties           # Build properties
├── gradlew                     # Gradle wrapper (Unix)
├── gradlew.bat                 # Gradle wrapper (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/               # Mod source code
│   │   └── resources/          # Assets and data
│   └── test/
│       └── java/               # Unit tests
├── build/                      # Build output (generated)
├── run/                        # Development runtime (generated)
└── docs/                       # Documentation
```

## Build Configuration

### build.gradle

```gradle
plugins {
    id 'net.neoforged.moddev' version '2.0.46-beta'
    id 'java'
}

version = '2.5.0'
group = 'com.zerofall.ezstorage'

base {
    archivesName = 'ezstorage'
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = '21.1.77'
    
    parchment {
        minecraftVersion = '1.21.1'
        mappingsVersion = '2024.11.17'
    }
    
    runs {
        client {
            client()
        }
        server {
            server()
        }
    }
    
    mods {
        ezstorage {
            sourceSet sourceSets.main
        }
    }
}

dependencies {
    // JEI integration
    compileOnly "mezz.jei:jei-1.21.1-common-api:19.19.6.233"
    compileOnly "mezz.jei:jei-1.21.1-neoforge-api:19.19.6.233"
    runtimeOnly "mezz.jei:jei-1.21.1-neoforge:19.19.6.233"
    
    // Test dependencies
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    testImplementation 'org.mockito:mockito-core:5.7.0'
}

test {
    useJUnitPlatform()
}
```

### gradle.properties

```properties
# Mod Properties
mod_version=2.5.0
mod_group_id=com.zerofall.ezstorage
mod_id=ezstorage
mod_name=EZStorage 2
mod_license=MIT
mod_url=https://github.com/zerofall/EZStorage2
mod_author=zerofall, SBlectric
mod_description=High-capacity storage system for Minecraft

# Build Properties
org.gradle.jvmargs=-Xmx4G
org.gradle.daemon=false
org.gradle.parallel=true
org.gradle.caching=true
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

rootProject.name = 'EZStorage2'
```

## Gradle Tasks

### Common Tasks

```bash
# Build the mod
./gradlew build

# Clean build artifacts
./gradlew clean

# Run development client
./gradlew runClient

# Run development server
./gradlew runServer

# Run unit tests
./gradlew test

# Generate IDE files
./gradlew genEclipseRuns  # For Eclipse
./gradlew genIntellijRuns # For IntelliJ
```

### Advanced Tasks

```bash
# Build with full clean
./gradlew clean build

# Run tests with detailed output
./gradlew test --info

# Build without daemon (CI/CD)
./gradlew build --no-daemon

# Refresh dependencies
./gradlew build --refresh-dependencies

# Generate source jar
./gradlew sourcesJar

# Generate javadoc
./gradlew javadoc
```

## Development Setup

### IntelliJ IDEA Setup

1. **Import Project**:
   - File → Open → Select `build.gradle`
   - Choose "Open as Project"
   - Wait for Gradle sync to complete

2. **Configure Run Configurations**:
   ```bash
   ./gradlew genIntellijRuns
   ```
   - Refresh Gradle project
   - Run configurations will appear in toolbar

3. **Set Java Version**:
   - File → Project Structure → Project
   - Set Project SDK to Java 21
   - Set Project language level to 21

### Eclipse Setup

1. **Import Project**:
   - File → Import → Existing Gradle Project
   - Select project root directory
   - Complete import wizard

2. **Generate Run Configurations**:
   ```bash
   ./gradlew genEclipseRuns
   ```
   - Refresh project in Eclipse
   - Run configurations will be available

## Dependency Management

### NeoForge Dependencies

**Automatic Dependencies**:
- Minecraft client/server
- NeoForge API
- Mappings (Parchment)

**Version Alignment**:
- NeoForge 21.1.77 → Minecraft 1.21.1
- Parchment mappings for deobfuscation
- Java 21 toolchain requirement

### External Dependencies

**JEI Integration**:
```gradle
compileOnly "mezz.jei:jei-1.21.1-common-api:19.19.6.233"
compileOnly "mezz.jei:jei-1.21.1-neoforge-api:19.19.6.233"
runtimeOnly "mezz.jei:jei-1.21.1-neoforge:19.19.6.233"
```

**Testing Framework**:
```gradle
testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
testImplementation 'org.mockito:mockito-core:5.7.0'
```

### Repository Configuration

```gradle
repositories {
    maven { name = 'NeoForged'; url = 'https://maven.neoforged.net/releases' }
    maven { name = 'Minecraft'; url = 'https://libraries.minecraft.net' }
    maven { name = 'ParchmentMC'; url = 'https://maven.parchmentmc.org' }
    maven { name = 'BlameJared'; url = 'https://maven.blamejared.com' }
    mavenCentral()
}
```

## Build Outputs

### Generated Artifacts

**Main Jar**:
- Location: `build/libs/ezstorage-2.5.0.jar`
- Contains: Compiled mod classes and resources
- Usage: Install in mods folder

**Sources Jar** (optional):
- Location: `build/libs/ezstorage-2.5.0-sources.jar`
- Contains: Source code for debugging
- Usage: IDE source attachment

**Javadoc Jar** (optional):
- Location: `build/libs/ezstorage-2.5.0-javadoc.jar`
- Contains: Generated API documentation
- Usage: Documentation reference

### Build Directory Structure

```
build/
├── classes/
│   ├── java/main/              # Compiled mod classes
│   └── java/test/              # Compiled test classes
├── resources/
│   ├── main/                   # Processed resources
│   └── test/                   # Test resources
├── libs/
│   └── ezstorage-2.5.0.jar    # Final mod jar
├── reports/
│   └── tests/                  # Test reports
└── tmp/                        # Temporary build files
```

## Development Workflow

### Typical Development Cycle

1. **Make Changes**: Edit source code
2. **Test Locally**: `./gradlew runClient`
3. **Run Tests**: `./gradlew test`
4. **Build**: `./gradlew build`
5. **Commit**: Git commit changes
6. **Release**: Tag and build release

### Hot Reload Development

**Client Development**:
```bash
./gradlew runClient
```
- Automatic resource reloading
- Fast iteration for GUI/texture changes
- Debug breakpoints supported

**Server Development**:
```bash
./gradlew runServer
```
- Test multiplayer functionality
- Validate networking code
- Performance testing

## Performance Optimization

### Build Performance

**Gradle Daemon**:
```properties
org.gradle.daemon=true          # Enable for development
org.gradle.daemon=false         # Disable for CI/CD
```

**Parallel Builds**:
```properties
org.gradle.parallel=true
org.gradle.workers.max=4        # Adjust based on CPU cores
```

**Memory Settings**:
```properties
org.gradle.jvmargs=-Xmx4G -XX:+UseG1GC
```

**Build Cache**:
```properties
org.gradle.caching=true
```

### Development Performance

**IDE Settings**:
- Increase IDE memory allocation
- Enable incremental compilation
- Configure proper exclusions for build directories

**JVM Arguments**:
```
-Xmx4G -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions
```

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
        path: build/libs/*.jar
```

## Troubleshooting

### Common Build Issues

**Dependency Resolution**:
```bash
./gradlew build --refresh-dependencies
```

**Clean Build**:
```bash
./gradlew clean build
```

**Gradle Wrapper Issues**:
```bash
./gradlew wrapper --gradle-version 8.10.2
```

**Memory Issues**:
- Increase `org.gradle.jvmargs` memory
- Close other applications
- Use `--no-daemon` for CI builds

### Version Compatibility

**NeoForge Versions**:
- Check compatibility matrix
- Update mappings with NeoForge version
- Verify JEI compatibility

**Java Versions**:
- Java 21+ required for NeoForge 1.21.1
- Update `java.toolchain.languageVersion`
- Verify IDE Java configuration

## Migration Notes

### From ForgeGradle

**Major Changes**:
- Plugin: `net.minecraftforge.gradle` → `net.neoforged.moddev`
- Configuration: Simplified setup
- Mappings: Integrated Parchment support
- Run configurations: Automatic generation

**Migration Steps**:
1. Update `build.gradle` plugin
2. Remove old ForgeGradle configuration
3. Add NeoForge configuration block
4. Update dependencies
5. Regenerate run configurations

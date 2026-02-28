# Dependency Management

## Current Dependencies

Last checked: 2026-01-14
Last updated: 2026-01-14

### Core Dependencies

| Dependency | Current Version | Latest Version | Status | Notes |
|------------|----------------|----------------|--------|-------|
| **NeoForge** | 21.1.218 | 21.1.218 | ✅ Current | Updated from 21.1.77 |
| **Minecraft** | 1.21.1 | 1.21.1 | ✅ Current | Target version |
| **ModDevGradle** | 2.0.139 | 2.0.139 | ✅ Current | Updated from 2.0.46-beta to stable |
| **Gradle** | 8.10.2 | 8.10.2 | ✅ Current | Via wrapper |
| **Java** | 21 | 21 | ✅ Current | Required for NeoForge |

### Runtime Dependencies

| Dependency | Current Version | Latest Version | Status | Notes |
|------------|----------------|----------------|--------|-------|
| **JEI (Just Enough Items)** | 19.27.0.336 | 19.27.0.336 | ✅ Current | Updated from 19.19.6.233 |

### Test Dependencies

| Dependency | Current Version | Latest Version | Status | Notes |
|------------|----------------|----------------|--------|-------|
| **JUnit Jupiter** | 5.11.4 | 5.11.4 | ✅ Current | Updated from 5.10.1 |
| **JUnit Platform Launcher** | (transitive) | (latest) | ✅ Auto-managed | Runtime only |

## Update Strategy

### Before Updating

1. **Backup current working state** - Commit all changes
2. **Review changelogs** - Check for breaking changes
3. **Test in development** - Run `./gradlew :neoforge:runClient` after update
4. **Run tests** - Execute `./gradlew :core:test` to verify compatibility

### Recent Updates

**2026-01-14**: Updated all dependencies to latest versions
- NeoForge: 21.1.77 → 21.1.218 (141 versions, bug fixes and stability)
- ModDevGradle: 2.0.46-beta → 2.0.139 (stable release)
- JEI: 19.19.6.233 → 19.27.0.336 (performance improvements)
- JUnit Jupiter: 5.10.1 → 5.11.4 (bug fixes)
- ✅ Build successful, all tests passing

### Update Priority

**High Priority:**
- NeoForge 21.1.77 → 21.1.218 (bug fixes, stability improvements)
- ModDevGradle 2.0.46-beta → 2.x stable (production-ready)

**Medium Priority:**
- JEI 19.19.6.233 → 19.27.0.336 (performance, new features)

**Low Priority:**
- JUnit Jupiter 5.10.1 → 5.11.4 (test framework only)

## Version Compatibility

### NeoForge Version Matrix

| Minecraft | NeoForge Range | Current | Latest Stable |
|-----------|---------------|---------|---------------|
| 1.21.1 | 21.1.x | 21.1.77 | 21.1.218 |
| 1.21.2 | 21.2.x | N/A | 21.2.x |
| 1.21.11 | 21.11.x | N/A | 21.11.x |

**Note**: Minecraft 1.21.1 is stable but no longer actively maintained. The 21.1.x line receives bug fixes only.

### JEI Compatibility

JEI 19.27.0.336 is confirmed compatible with:
- NeoForge 21.1.x (all versions)
- Minecraft 1.21.1

## Maven Repositories

### NeoForge
```
https://maven.neoforged.net/releases/
```

### JEI (Blamejared)
```
https://maven.blamejared.com/
```

### Maven Central (JUnit)
```
https://repo.maven.apache.org/maven2/
```

## Checking for Updates

### Manual Check

**NeoForge versions:**
```bash
curl -s "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml" | grep -E "<version>21\.1\." | tail -5
```

**JEI versions:**
Visit [Modrinth JEI page](https://modrinth.com/mod/jei/versions) and filter by NeoForge 1.21.1

**JUnit versions:**
Visit [Maven Central](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter)

### Automated Check

Consider adding a Gradle task to check for dependency updates:
```gradle
// Future: Add dependency update checker plugin
```

## Update History

| Date | Component | From | To | Notes |
|------|-----------|------|-----|-------|
| 2026-01-14 | NeoForge | 21.1.77 | 21.1.218 | Bug fixes and stability improvements |
| 2026-01-14 | ModDevGradle | 2.0.46-beta | 2.0.139 | Stable release |
| 2026-01-14 | JEI | 19.19.6.233 | 19.27.0.336 | Performance improvements, bookmarking support |
| 2026-01-14 | JUnit Jupiter | 5.10.1 | 5.11.4 | Bug fixes and improvements |
| 2026-01-13 | Initial Release | - | 1.0.0 | Port complete with NeoForge 21.1.77 |

## References

- [NeoForge Releases](https://neoforged.net/categories/releases/)
- [NeoForge Maven Repository](https://maven.neoforged.net/releases/net/neoforged/neoforge/)
- [JEI on Modrinth](https://modrinth.com/mod/jei)
- [ModDevGradle Documentation](https://docs.neoforged.net/toolchain/docs/plugins/mdg/)

# Steve's Simple Storage (S3) - Technical Documentation

This directory contains comprehensive technical documentation for the Steve's Simple Storage port from Minecraft 1.12.2 to 1.21.1 with NeoForge.

## Documentation Structure

- **[Port Overview](port-overview.md)** - High-level overview of the porting process and major changes
- **[Storage System](storage-system.md)** - Detailed storage system implementation
- **[GUI System](gui-system.md)** - User interface implementation and layout
- **[Configuration](configuration.md)** - TOML configuration system and options
- **[Build System](build-system.md)** - Gradle configuration and build process
- **[Dependencies](dependencies.md)** - Dependency versions and update tracking
- **[Troubleshooting](troubleshooting.md)** - Common issues and solutions
- **[Changelog](../CHANGELOG.md)** - Version history and changes

## Quick Reference

### Key Technologies
- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.218
- **ModDevGradle**: 2.0.139
- **Java**: 21
- **Gradle**: 8.10.2

### Project Status
✅ **Version 0.1.0-beta** - Core storage functionality complete and functional

**Implemented Features:**
- Basic storage system with multiblock capacity scaling
- Storage Core GUI with sidebar item display and scrolling
- Tiered storage blocks (Storage Box, Condensed, Compressed, Super, Ultra, Hyper, Ultimate)
- Item insertion and extraction with proper client-server sync
- JEI integration for recipe transfer and ingredient lookup
- Crafting Box with 3x3 crafting grid connected to storage
- Shift-click crafting with automatic ingredient repopulation
- Search Box with real-time filtering (name, tags, mod, tooltips)
- Dolly and Key items

**Planned Features (Not Yet Ported):**
- Security Box with player access control
- Sort Box with automatic sorting modes
- Access Terminal for remote access
- Input/Extract/Eject Ports for automation
- Blank Box for decorative multiblock components

### Getting Started
1. See [Build System](build-system.md) for setup instructions
2. Review [Architecture](architecture.md) for system overview
3. Check [Troubleshooting](troubleshooting.md) for common issues

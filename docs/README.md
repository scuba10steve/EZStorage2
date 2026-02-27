# Steve's Simple Storage (S3) - Technical Documentation

This directory contains comprehensive technical documentation for Steve's Simple Storage, a Minecraft mod built on NeoForge.

## Documentation Structure

- **[Port Overview](port-overview.md)** - High-level overview of the porting process and major changes
- **[Storage System](storage-system.md)** - Detailed storage system implementation
- **[GUI System](gui-system.md)** - User interface implementation and layout
- **[Configuration](configuration.md)** - TOML configuration system and options
- **[Build System](build-system.md)** - Multi-module Gradle configuration and build process
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

### Architecture
The project uses a **multi-module Gradle layout**:
- **`common`** - Platform-agnostic code (vanilla MC classes only via `neoFormVersion`)
- **`neoforge`** - NeoForge-specific code (registration, config, packet handlers, JEI)

A platform abstraction layer (`S3Platform`, `S3Config`, `S3NetworkHelper`) decouples common code from loader-specific APIs.

### Project Status
✅ **Version 0.3.1** - All features ported and functional

**Implemented Features:**
- Basic storage system with multiblock capacity scaling
- Storage Core GUI with sidebar item display and scrolling
- Tiered storage blocks (Storage Box, Condensed, Compressed, Super, Ultra, Hyper, Ultimate)
- Item insertion and extraction with proper client-server sync
- JEI integration for recipe transfer and ingredient lookup
- Crafting Box with 3x3 crafting grid connected to storage
- Shift-click crafting with automatic ingredient repopulation
- Search Box with real-time filtering (name, tags, mod, tooltips)
- Sort Box with automatic sorting modes
- Security Box with player access control
- Access Terminal for remote access
- Input/Extract/Eject Ports for automation
- Blank Box for decorative multiblock components
- Dolly and Key items

### Getting Started
1. See [Build System](build-system.md) for setup instructions
2. Check [Troubleshooting](troubleshooting.md) for common issues

# EZStorage 2 - Technical Documentation

This directory contains comprehensive technical documentation for the EZStorage 2 port from Minecraft 1.12.2 to 1.21.1 with NeoForge.

## Documentation Structure

- **[Port Overview](port-overview.md)** - High-level overview of the porting process and major changes
- **[Architecture](architecture.md)** - System architecture and component relationships
- **[Storage System](storage-system.md)** - Detailed storage system implementation
- **[GUI System](gui-system.md)** - User interface implementation and layout
- **[Networking](networking.md)** - Client-server communication and packet system
- **[Multiblock System](multiblock-system.md)** - Multiblock structure detection and validation
- **[Configuration](configuration.md)** - TOML configuration system and options
- **[JEI Integration](jei-integration.md)** - Just Enough Items integration details
- **[Asset System](asset-system.md)** - Textures, models, recipes, and language files
- **[Build System](build-system.md)** - Gradle configuration and build process
- **[Testing](testing.md)** - Unit tests and testing methodology
- **[Troubleshooting](troubleshooting.md)** - Common issues and solutions

## Quick Reference

### Key Technologies
- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.77
- **ModDevGradle**: 2.0.46-beta
- **Java**: 21
- **Gradle**: 8.10.2

### Project Status
✅ **Version 1.0.0** - Core storage functionality complete and functional

**Implemented Features:**
- Basic storage system with multiblock capacity scaling
- Storage Core GUI with sidebar item display and scrolling
- Tiered storage blocks (Storage Box, Condensed, Super, Ultra, Hyper)
- Item insertion and extraction with proper client-server sync
- JEI integration for recipe transfer
- Dolly and Key items

**Planned Features (Not Yet Ported):**
- Crafting Box with crafting grid integration
- Search Box with item filtering
- Security Box with player access control
- Sort Box with automatic sorting modes
- Access Terminal for remote access
- Input/Extract/Eject Ports for automation
- Blank Box for decorative multiblock components

### Getting Started
1. See [Build System](build-system.md) for setup instructions
2. Review [Architecture](architecture.md) for system overview
3. Check [Troubleshooting](troubleshooting.md) for common issues

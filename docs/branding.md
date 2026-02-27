# Branding Guide - Steve's Simple Storage

## Status: Rebranding Complete ✅

The mod has been successfully rebranded from **EZStorage 2** to **Steve's Simple Storage (S3)**. All package structures, mod IDs, and asset directories have been updated.

## Brand Identity

**Name**: Steve's Simple Storage
**Abbreviation**: S3
**Version**: 0.3.1
**Tagline**: TBD

## Inspiration

The name is inspired by AWS S3 (Simple Storage Service), creating a fun parallel between cloud storage and Minecraft storage systems. The personal connection (developer's name is also Steve) adds authenticity to the Minecraft Steve reference.

## Branding Decisions

### Core Identity
- **Mod Name**: Steve's Simple Storage
- **Mod ID**: `s3` ✅ (updated)
- **Display Name**: Steve's Simple Storage ✅ (updated)
- **Short Name**: S3 Storage
- **Package Structure**: `io.github.scuba10steve.s3` ✅ (updated)

### Tagline Options
1. "Simple Storage for Minecraft"
2. "Steve's favorite storage solution"
3. "Storage made simple"
4. "Your simple storage solution"

**Selected**: _[To be decided]_

### Technical Naming
- **Keep or Rebrand Block Names?**
  - Option A: Keep technical names (Storage Core, Storage Box, etc.)
  - Option B: Rebrand to S3 theme (S3 Core, S3 Box, Simple Storage Core, etc.)
  
**Selected**: _[To be decided]_

### Java Class Naming — COMPLETED ✅

All EZ-prefixed classes have been renamed. The naming convention uses a mix of "S3", "Storage", and "Mod" prefixes:

| Old Name | New Name | Module |
|----------|----------|--------|
| `EZStorage` | `StevesSimpleStorage` | neoforge |
| `EZConfig` | `StorageConfig` | neoforge |
| `EZInventory` | `StorageInventory` | common |
| `EZBlock` | `BaseBlock` | common |
| `EZBlockEntity` | (removed — not needed) | — |
| `EZItem` | `BaseItem` | common |
| `EZTab` | `ModTab` | neoforge |
| `EZNetwork` | `ModNetwork` | neoforge |
| `EZStorageJEIPlugin` | `S3JEIPlugin` | neoforge |
| `EZBlocks` | `ModBlocks` | neoforge |
| `EZItems` | `ModItems` | neoforge |
| `EZMenuTypes` | `ModMenuTypes` | neoforge |
| `EZBlockEntities` | `ModBlockEntities` | neoforge |

### Package Structure
- **Current**: `io.github.scuba10steve.s3` ✅ (updated from `io.github.scuba10steve.ezstorage`)
- **Decision**: Completed - Full package rename for consistency

## Rebranding Checklist

### High Priority - COMPLETED ✅
- [x] `gradle.properties` - Updated mod_name and mod_description
- [x] `neoforge/src/main/resources/META-INF/neoforge.mods.toml` - Updated displayName and description
- [x] `common/src/main/resources/assets/s3/lang/en_us.json` - Updated mod name and descriptions
- [x] Package structure - Renamed from `io.github.scuba10steve.ezstorage` to `io.github.scuba10steve.s3`
- [x] Mod ID - Changed from `ezstorage` to `s3`
- [x] Asset directories - Renamed from `assets/ezstorage/` to `assets/s3/`
- [x] Data directories - Renamed from `data/ezstorage/` to `data/s3/`

### Medium Priority - COMPLETED ✅
- [x] Creative tab display name - Updated to "Steve's Simple Storage"
- [x] In-game tooltips and descriptions
- [x] JEI integration display names
- [x] Java class names - All EZ-prefixed classes renamed (see table above)

### Low Priority
- [ ] Logo/icon design (128x128 for mod icon)
- [ ] Banner image for mod pages
- [ ] Social media graphics

### Optional/Future
- [ ] Custom texture overlays with S3 branding
- [ ] Themed block names
- [ ] Easter eggs referencing AWS services

## Visual Identity

### Color Scheme
- **Primary**: TBD
- **Secondary**: TBD
- **Accent**: TBD

### Logo Concepts
- S3 text with storage box icon
- Minecraft chest with "S3" label
- Steve character with storage blocks
- Cloud storage + Minecraft blocks hybrid

**Selected**: _[To be decided]_

## Messaging

### Elevator Pitch
"Steve's Simple Storage (S3) is a Minecraft mod that provides massive, scalable storage through an intuitive multiblock system. Inspired by AWS S3, it makes storing millions of items simple and accessible."

### Key Features to Highlight
1. **Simple** - Easy to use, intuitive interface
2. **Scalable** - Grows with your needs (10K to 2.5M+ items)
3. **Smart** - JEI integration, automatic organization
4. **Stable** - Reliable, well-tested core functionality

### Target Audience
- Players who need massive storage solutions
- Modpack creators looking for reliable storage mods
- Technical Minecraft players who appreciate the AWS reference
- Anyone tired of chest clutter

## Implementation Notes

### Files Updated

**Configuration Files:**
- `gradle.properties`
- `neoforge/src/main/resources/META-INF/neoforge.mods.toml`

**Language Files:**
- `common/src/main/resources/assets/s3/lang/en_us.json`

**Documentation:**
- All docs/*.md files updated

**Code:**
- All EZ-prefixed classes renamed
- Creative tab in `neoforge/.../ref/ModTab.java`
- All display strings updated

## Future Branding Opportunities

### Expansion Names (Future Features)
When implementing additional features, consider AWS-themed names:
- **Crafting Box** → "Lambda Crafter" (serverless computing)
- **Search Box** → "CloudSearch Box" (AWS CloudSearch)
- **Security Box** → "IAM Box" (Identity and Access Management)
- **Sort Box** → "Kinesis Sorter" (data streaming/sorting)
- **Access Terminal** → "CloudFront Terminal" (content delivery)

### Community Engagement
- Emphasize the AWS connection in mod descriptions
- Create comparison charts (S3 features vs mod features)
- Use AWS terminology in documentation where appropriate
- Engage with both Minecraft and tech communities

## Notes

- Mod ID is `s3` (fully migrated from `ezstorage`)
- Package is `io.github.scuba10steve.s3`
- All EZ-prefixed classes have been renamed
- Project uses multi-module layout: `common/` (platform-agnostic) and `neoforge/` (loader-specific)

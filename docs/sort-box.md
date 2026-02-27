# Sort Box Feature Documentation

## Overview

The Sort Box is a multiblock component that enables automatic sorting of items in the Storage Core GUI. When placed adjacent to a Storage Core multiblock, it adds a sort button to the GUI that allows players to cycle through different sorting modes.

## Implementation Status

✅ **COMPLETE** - The Sort Box is fully implemented and functional.

## Features

### 6 Sorting Modes

1. **Count Down** - Sorts by descending item counts, then A-Z for equal cases
2. **Count Up** - Sorts by ascending item counts, then Z-A for equal cases
3. **Name A-Z** - Sorts alphabetically A-Z, then by descending item counts for equal cases
4. **Name Z-A** - Sorts alphabetically Z-A, then by ascending item counts for equal cases
5. **Mod A-Z** - Sorts by mod name A-Z, then by descending item counts for equal cases
6. **Mod Z-A** - Sorts by mod name Z-A, then by ascending item counts for equal cases

### GUI Integration

- Sort button appears in the Storage Core GUI when a Sort Box is present in the multiblock
- Button displays the current sort mode name
- Clicking the button cycles through all 6 modes
- Button is positioned below the storage grid area
- Sort mode persists when the GUI is closed and reopened

### Technical Details

#### Key Classes

- **`BlockSortBox`** - The block class for the Sort Box
- **`SortBoxBlockEntity`** - Block entity that marks the multiblock as having sort capability
- **`SortMode`** - Enum containing all 6 sorting modes with comparators
- **`SortModePacket`** - Network packet for syncing sort mode changes
- **`AbstractStorageScreen`** - GUI screen that renders the sort button and handles clicks

#### Multiblock Detection

The Storage Core scans for adjacent Sort Box blocks during multiblock validation:
- When a Sort Box is found, `hasSortBox` flag is set to `true`
- This flag is synced to clients via `StorageSyncPacket`
- The GUI checks this flag to show/hide the sort button

#### Sorting Logic

- Sorting is applied in `StorageInventory.getSortedItems()`
- Items are sorted using the comparator from the current `SortMode`
- Sorting is applied when:
  - Displaying items in the GUI
  - JEI integration queries the storage
  - Search results are displayed (sorted items are filtered)

#### Network Synchronization

1. Player clicks sort button in GUI
2. Client sends `SortModePacket` to server
3. Server rotates to next sort mode
4. Server broadcasts `StorageSyncPacket` to all tracking clients
5. Clients update their local inventory and GUI

### Recipe

The Sort Box is crafted with:
- 4x Wood Logs (any type)
- 4x Comparators
- 1x Storage Box (center)

### Assets

- **Texture**: `assets/s3/textures/block/sort_box.png`
- **Model**: `assets/s3/models/block/sort_box.json`
- **Blockstate**: `assets/s3/blockstates/sort_box.json`
- **Recipe**: `data/s3/recipes/sort_box.json`
- **Localization**: `assets/s3/lang/en_us.json`

## Testing

Unit tests are provided in `SortModeTest.java`:
- ✅ Mode rotation (cycles through all 6 modes)
- ✅ Ordinal conversion with wrap-around
- ✅ All modes have display names, descriptions, and comparators
- ✅ Exactly 6 modes exist

Run tests with:
```bash
./gradlew :common:test --tests "io.github.scuba10steve.s3.util.SortModeTest"
```

## Usage

1. Place a Storage Core and build a multiblock with Storage Boxes
2. Add a Sort Box adjacent to any block in the multiblock
3. Open the Storage Core GUI
4. Click the sort button (appears below the storage grid)
5. Items will be sorted according to the selected mode
6. Click again to cycle to the next mode

## Integration with Other Features

- **Search Box**: When both Search and Sort boxes are present, search results are displayed in sorted order
- **JEI Integration**: JEI queries use sorted items when Sort Box is present
- **Crafting Box**: Crafting grid works seamlessly with sorted storage

## Future Enhancements

Potential improvements (not currently planned):
- Custom sort modes defined by players
- Sort by item rarity/tier
- Sort by recently added items
- Configurable default sort mode

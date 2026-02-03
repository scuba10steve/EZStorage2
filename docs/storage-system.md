# Storage System Architecture

## Overview

The Steve's Simple Storage system provides massive item storage capacity through a multiblock network architecture. Items are stored in a centralized inventory system with support for quantities up to `Long.MAX_VALUE`.

## Core Components

### EZInventory
**Location**: `io.github.scuba10steve.s3.storage.EZInventory`

The central storage inventory that manages all stored items.

**Key Features**:
- Stores items as `StoredItemStack` objects with large quantity support
- Dynamic capacity based on connected storage blocks
- Efficient item merging for same item types
- Insert/extract operations with remainder handling

**Methods**:
```java
ItemStack insertItem(ItemStack stack)  // Insert items, returns remainder
ItemStack extractItem(ItemStack template, int amount)  // Extract items
List<StoredItemStack> getStoredItems()  // Get all stored items
long getTotalItemCount()  // Get current item count
void setMaxItems(long maxItems)  // Set storage capacity
```

**Logging**:
- Debug level: Detailed operation tracking
- Info level: Capacity changes
- Tracks: Item types, counts, available space, merge operations

### StoredItemStack
**Location**: `io.github.scuba10steve.s3.storage.StoredItemStack`

Wrapper class for storing items with large quantities.

**Structure**:
```java
private final ItemStack itemStack;  // Template item (count=1)
private long count;  // Actual quantity stored
```

**Benefits**:
- Supports quantities beyond ItemStack's int limit
- Efficient memory usage (single template per item type)
- Simple count manipulation without ItemStack overhead

### StorageCoreBlockEntity
**Location**: `io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity`

The block entity that manages the storage system and multiblock network.

**Responsibilities**:
- Owns the EZInventory instance
- Scans and validates multiblock structures
- Handles item insertion/extraction
- Syncs storage data to clients
- Provides GUI access via MenuProvider

**Key Methods**:
```java
void scanMultiblock()  // Scan connected blocks and calculate capacity
ItemStack insertItem(ItemStack stack)  // Insert items into storage
ItemStack extractItem(ItemStack template, int amount)  // Extract items
EZInventory getInventory()  // Get inventory instance
```

## Storage Capacity System

### Capacity Calculation

Storage capacity is calculated by summing all connected storage blocks:

```java
long totalCapacity = 0;
for (BlockRef blockRef : multiblock) {
    if (blockRef.block instanceof BlockStorage storage) {
        totalCapacity += storage.getCapacity();
    }
}
inventory.setMaxItems(totalCapacity);
```

### Storage Tiers

| Tier | Capacity | Material | Multiplier |
|------|----------|----------|------------|
| Basic | 10,000 | Wood/Chest | 1x |
| Condensed | 40,000 | Cobblestone | 4x |
| Compressed | 80,000 | Copper | 2x |
| Super | 160,000 | Iron | 2x |
| Ultra | 640,000 | Gold | 4x |
| Hyper | 2,560,000 | Diamond | 4x |
| Ultimate | 10,240,000 | Netherite | 4x |

### Capacity Scaling

Each tier provides increased capacity, allowing for exponential storage growth as players progress through the game.

## Item Storage Logic

### Insertion Process

1. **Validation**: Check if stack is empty
2. **Capacity Check**: Verify available space
3. **Merge Attempt**: Try to merge with existing stacks
4. **New Entry**: Create new StoredItemStack if no merge possible
5. **Remainder**: Return any items that couldn't be stored

```java
// Simplified insertion logic
if (totalCount >= maxItems) return stack;  // Full

long availableSpace = maxItems - totalCount;
int insertAmount = (int) Math.min(availableSpace, stack.getCount());

// Try to merge with existing
for (StoredItemStack stored : items) {
    if (ItemStack.isSameItemSameComponents(stored.getItemStack(), stack)) {
        stored.addCount(insertAmount);
        return remainder;
    }
}

// Create new entry
items.add(new StoredItemStack(stack.copyWithCount(1), insertAmount));
```

### Extraction Process

1. **Find Matching Item**: Search for item in storage
2. **Calculate Amount**: Determine how much to extract
3. **Update Count**: Reduce stored count
4. **Remove Empty**: Remove entry if count reaches zero
5. **Return Stack**: Create ItemStack with extracted amount

## GUI Integration

### StorageSlot
**Location**: `io.github.scuba10steve.s3.gui.slot.StorageSlot`

Custom slot implementation for storage GUI interaction.

**Features**:
- Intercepts item placement to insert into storage
- Handles drag & drop operations
- Integrates with shift-click functionality
- Returns remainder items to player

**Key Methods**:
```java
boolean mayPlace(ItemStack stack)  // Allow all non-empty stacks
void set(ItemStack stack)  // Insert items into storage
ItemStack remove(int amount)  // Not supported for storage slots
```

### StorageCoreMenu
**Location**: `io.github.scuba10steve.s3.gui.server.StorageCoreMenu`

Container menu that provides 54 storage slots for item management.

**Layout**:
- **Storage Slots**: 54 slots (6 rows × 9 columns) at Y=18-125
- **Player Inventory**: 27 slots (3 rows × 9 columns) at Y=140-194
- **Player Hotbar**: 9 slots at Y=198

**Shift-Click Logic**:
```java
if (index < 54) {
    // From storage to player
    moveItemStackTo(slotStack, 54, slots.size(), true);
} else {
    // From player to storage
    moveItemStackTo(slotStack, 0, 54, false);
}
```

## Synchronization

### Client-Server Communication

Storage data is synchronized using the `StorageSyncPacket`:

**When Synced**:
- After item insertion
- After item extraction
- After multiblock scan
- When player opens GUI

**Packet Contents**:
- Block position of storage core
- List of all StoredItemStack entries
- Item types and quantities

**Sync Method**:
```java
private void syncToClients() {
    if (level instanceof ServerLevel serverLevel) {
        PacketDistributor.sendToPlayersTrackingChunk(
            serverLevel, 
            level.getChunkAt(worldPosition).getPos(),
            new StorageSyncPacket(worldPosition, inventory.getStoredItems())
        );
    }
}
```

## Performance Considerations

### Memory Efficiency
- Single ItemStack template per item type (not per item)
- Long-based counting instead of multiple ItemStacks
- Efficient list operations for item lookup

### Network Efficiency
- Only sync when storage changes
- Send to players tracking chunk (not all players)
- Compact packet format with StreamCodec

### Scalability
- Supports millions of items per storage type
- Efficient multiblock scanning with visited set
- O(n) insertion/extraction where n = unique item types

## Debugging

### Logging Levels

**DEBUG**:
- Individual item operations
- Capacity checks
- Merge attempts
- Slot interactions

**INFO**:
- Multiblock scans
- Capacity changes
- Major operations

### Log Examples

```
[INFO] Scanning multiblock at BlockPos{x=100, y=64, z=200}
[DEBUG] Found storage block at BlockPos{x=101, y=64, z=200} with capacity 10000
[INFO] Multiblock scan complete. Found 5 blocks, total capacity: 50000
[DEBUG] EZInventory.insertItem: minecraft:cobblestone x64, current capacity: 0/50000
[DEBUG] Creating new storage entry for minecraft:cobblestone x64
```

## Implemented Enhancements

- **Search System** ✅: Quick item lookup via Search Box with multiple search modes

## Future Enhancements

### Potential Improvements
- **Item Filtering**: Filter what items can be stored
- **Auto-Sorting**: Automatic item organization (Sort Box)
- **Void Overflow**: Option to void items when full
- **Priority System**: Preferred storage locations for specific items

### Networked Storage (Future Concept)
Connect multiple Storage Core systems together to create a unified storage network.

**Potential Approaches:**
- **Network Bridge Block**: Physical block that links two Storage Cores within range
- **Redstone Conduit**: Cable-based connections between storage systems
- **Wireless Transmission**: Ender-themed wireless links for long-distance/cross-dimensional connections

**Considerations:**
- Shared vs. partitioned inventory views
- Bandwidth/transfer rate limits
- Security across network nodes
- Performance implications of large networks
- Visual feedback for network status

*Note: This is a conceptual feature for future development. No implementation work has been started.*

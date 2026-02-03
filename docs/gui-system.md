# GUI System Implementation

## Overview

The Steve's Simple Storage GUI system provides a modern Minecraft inventory interface for storage management. The system uses the standard Container/Screen architecture with custom slots for storage interaction.

## Architecture

### Container-Screen Pattern

**Server Side**: `StorageCoreMenu` (Container)
- Manages slot layout and item transfer logic
- Handles shift-click operations
- Validates player access

**Client Side**: `StorageCoreScreen` (Screen)
- Renders GUI background and elements
- Handles user input and rendering
- Displays tooltips and labels

## Components

### StorageCoreMenu
**Location**: `io.github.scuba10steve.s3.gui.server.StorageCoreMenu`

The server-side container that manages the storage GUI.

**Slot Layout**:
```java
// Storage slots: 54 slots (6 rows × 9 columns)
for (int row = 0; row < 6; row++) {
    for (int col = 0; col < 9; col++) {
        int index = col + row * 9;
        addSlot(new StorageSlot(storageContainer, blockEntity, index, 
                8 + col * 18, 18 + row * 18));
    }
}

// Player inventory: 27 slots (3 rows × 9 columns)
for (int i = 0; i < 3; ++i) {
    for (int j = 0; j < 9; ++j) {
        addSlot(new Slot(playerInventory, j + i * 9 + 9, 
                8 + j * 18, 140 + i * 18));
    }
}

// Player hotbar: 9 slots
for (int i = 0; i < 9; ++i) {
    addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
}
```

**Shift-Click Logic**:
```java
public ItemStack quickMoveStack(Player player, int index) {
    Slot slot = this.slots.get(index);
    if (slot != null && slot.hasItem()) {
        ItemStack slotStack = slot.getItem();
        
        if (index < 54) {
            // Moving from storage to player inventory
            moveItemStackTo(slotStack, 54, this.slots.size(), true);
        } else {
            // Moving from player inventory to storage
            moveItemStackTo(slotStack, 0, 54, false);
        }
    }
}
```

### StorageCoreScreen
**Location**: `io.github.scuba10steve.s3.gui.client.StorageCoreScreen`

The client-side screen that renders the storage GUI.

**Configuration**:
```java
public StorageCoreScreen(StorageCoreMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageHeight = 222; // Height for 6 rows + player inventory
    this.imageWidth = 176;  // Standard width
    
    // Position labels to prevent overlap
    this.titleLabelY = 6;                    // Top of GUI
    this.inventoryLabelY = this.imageHeight - 94; // Above player inventory
}
```

**Rendering**:
```java
protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
}
```

### StorageSlot
**Location**: `io.github.scuba10steve.s3.gui.slot.StorageSlot`

Custom slot implementation for storage interaction.

**Key Features**:
- **Item Insertion**: Intercepts item placement to store in EZInventory
- **Drag & Drop**: Supports standard Minecraft drag operations
- **Remainder Handling**: Returns items that couldn't be stored
- **Logging**: Comprehensive operation tracking

**Implementation**:
```java
public void set(ItemStack stack) {
    if (!stack.isEmpty() && storageCore != null) {
        ItemStack remainder = storageCore.insertItem(stack);
        super.set(remainder);  // Show remainder in slot
    } else {
        super.set(stack);
    }
}

public boolean mayPlace(ItemStack stack) {
    return !stack.isEmpty();  // Accept all non-empty stacks
}
```

## Layout Specifications

### Coordinate System

The GUI uses a pixel-based coordinate system with the following layout:

```
┌─────────────────────────────────────────────────────────────────────┐
│ Title (Y=6)                                                         │
├─────────────────────────────────────────────────────────────────────┤
│ Storage Slots (6 rows × 9 columns)                                 │
│ Y=18 to Y=125, X=8 to X=170                                        │
│ ┌───┬───┬───┬───┬───┬───┬───┬───┬───┐                               │
│ │ 0 │ 1 │ 2 │ 3 │ 4 │ 5 │ 6 │ 7 │ 8 │ Row 0 (Y=18)                │
│ ├───┼───┼───┼───┼───┼───┼───┼───┼───┤                               │
│ │ 9 │10 │11 │12 │13 │14 │15 │16 │17 │ Row 1 (Y=36)                │
│ │...│...│...│...│...│...│...│...│...│                               │
│ │45 │46 │47 │48 │49 │50 │51 │52 │53 │ Row 5 (Y=108)               │
│ └───┴───┴───┴───┴───┴───┴───┴───┴───┘                               │
├─────────────────────────────────────────────────────────────────────┤
│ Inventory Label (Y=128)                                             │
├─────────────────────────────────────────────────────────────────────┤
│ Player Inventory (3 rows × 9 columns)                              │
│ Y=140 to Y=194, X=8 to X=170                                       │
├─────────────────────────────────────────────────────────────────────┤
│ Player Hotbar (1 row × 9 columns)                                  │
│ Y=198, X=8 to X=170                                                │
└─────────────────────────────────────────────────────────────────────┘
Total Height: 222 pixels
Total Width: 176 pixels
```

### Slot Positioning Formula

**Storage Slots**:
```java
int x = 8 + column * 18;  // Columns 0-8
int y = 18 + row * 18;    // Rows 0-5
```

**Player Inventory**:
```java
int x = 8 + column * 18;  // Columns 0-8
int y = 140 + row * 18;   // Rows 0-2
```

**Player Hotbar**:
```java
int x = 8 + column * 18;  // Columns 0-8
int y = 198;              // Single row
```

## Texture System

### GUI Texture
**Location**: `assets/s3/textures/gui/storage_core.png`
- **Size**: 256×256 pixels
- **Format**: PNG with transparency support
- **Usage**: Background for storage GUI

### Texture Coordinates
The GUI texture uses standard Minecraft GUI conventions:
- **Background**: Full texture area
- **Slot Highlights**: Standard 18×18 pixel slots
- **Borders**: 1-pixel borders around slot areas

## User Interactions

### Item Insertion Methods

1. **Drag & Drop**:
   - Player drags item from inventory to storage slot
   - `StorageSlot.set()` intercepts and inserts into storage
   - Remainder appears in slot if storage becomes full

2. **Shift-Click**:
   - Player shift-clicks item in player inventory
   - `quickMoveStack()` automatically finds available storage slot
   - Items transfer automatically without dragging

3. **Direct Placement**:
   - Player clicks item stack on storage slot
   - Standard Minecraft slot behavior with storage integration

### Item Retrieval Methods

1. **Click & Drag**:
   - Currently not implemented (storage slots are insert-only)
   - Future enhancement: Allow retrieval of stored items

2. **Shift-Click**:
   - Currently not implemented for storage slots
   - Future enhancement: Transfer stored items to player inventory

## Menu Registration

### Menu Type Registration
**Location**: `io.github.scuba10steve.s3.init.EZMenuTypes`

```java
public static final Supplier<MenuType<StorageCoreMenu>> STORAGE_CORE = 
    MENU_TYPES.register("storage_core", () -> 
        IMenuTypeExtension.create((windowId, inv, data) -> 
            new StorageCoreMenu(windowId, inv, data.readBlockPos())));
```

### Screen Registration
**Location**: `io.github.scuba10steve.s3.events.ClientEvents`

```java
@SubscribeEvent
public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(EZMenuTypes.STORAGE_CORE.get(), StorageCoreScreen::new);
}
```

## Debugging

### GUI Logging

**Menu Operations**:
```java
LOGGER.debug("Creating StorageCoreMenu at {}", pos);
LOGGER.debug("quickMoveStack: slot {}", index);
```

**Slot Operations**:
```java
LOGGER.debug("StorageSlot.mayPlace: {} x{}", stack.getItem(), stack.getCount());
LOGGER.debug("StorageSlot.set: {} x{}", stack.getItem(), stack.getCount());
```

### Common Issues

1. **Items Disappearing**:
   - Check `StorageSlot.set()` implementation
   - Verify remainder handling
   - Check storage capacity

2. **Shift-Click Not Working**:
   - Verify `quickMoveStack()` slot index ranges
   - Check `moveItemStackTo()` parameters
   - Ensure proper slot registration

3. **GUI Layout Issues**:
   - Verify slot positioning coordinates
   - Check GUI texture dimensions
   - Validate label positioning

## Future Enhancements

### Implemented Features

1. **Item Retrieval** ✅:
   - Click storage slots to retrieve items
   - Shift-click to move to player inventory
   - Right-click for half stack retrieval

2. **Search System** ✅ (requires Search Box in multiblock):
   - Search bar for finding specific items
   - Real-time filter display based on search terms
   - Multiple search modes: standard, `$` tags, `@` mod, `%` name

### Planned Features

1. **Sorting Options**:
   - Sort by item name, quantity, or type
   - Auto-sort functionality
   - Custom sorting preferences

2. **Visual Enhancements**:
   - Item quantity overlays for large stacks
   - Progress bars for storage capacity
   - Visual indicators for full/empty slots

### Technical Improvements

1. **Performance**:
   - Lazy loading of storage slot contents
   - Efficient rendering for large inventories
   - Optimized network synchronization

2. **Accessibility**:
   - Keyboard navigation support
   - Screen reader compatibility
   - Customizable GUI scaling

3. **Integration**:
   - JEI recipe viewing from storage slots
   - Waila/Hwyla tooltip integration
   - REI compatibility for recipe lookup

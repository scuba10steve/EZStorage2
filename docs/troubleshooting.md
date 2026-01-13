# Troubleshooting Guide

## Common Issues and Solutions

This guide covers common issues encountered during development and usage of EZStorage 2, along with their solutions.

## Build Issues

### Gradle Build Failures

**Issue**: Build fails with dependency resolution errors
```
Could not resolve all files for configuration ':compileClasspath'
```

**Solution**:
1. Check internet connection
2. Clear Gradle cache: `./gradlew clean --refresh-dependencies`
3. Verify repository accessibility in `build.gradle`
4. Check NeoForge/JEI version compatibility

**Issue**: Java version mismatch
```
Unsupported class file major version
```

**Solution**:
1. Ensure Java 21 is installed and active
2. Set `JAVA_HOME` environment variable
3. Verify Gradle is using correct Java version: `./gradlew -version`

### Compilation Errors

**Issue**: Missing imports or class not found
```
cannot find symbol: class SimpleContainer
```

**Solution**:
1. Check import statements match NeoForge 1.21.1 API
2. Verify class names haven't changed between versions
3. Consult NeoForge documentation for correct imports

**Issue**: Method signature mismatch
```
method does not override or implement a method from a supertype
```

**Solution**:
1. Check method signatures against parent class
2. Verify return types match expected types
3. Update to modern Minecraft method signatures

## Runtime Issues

### Mod Loading Problems

**Issue**: Mod fails to load
```
Failed to load mod ezstorage
```

**Solution**:
1. Check `neoforge.mods.toml` for correct metadata
2. Verify main mod class is properly annotated with `@Mod`
3. Check logs for specific error messages
4. Ensure all required dependencies are present

**Issue**: Missing textures (black/purple checkerboard)
```
Missing textures in model ezstorage:storage_box#inventory
```

**Solution**:
1. Verify texture files exist in correct directories
2. Check texture paths in model files use `block/` not `blocks/`
3. Ensure texture files are valid PNG format
4. Verify model files reference correct texture paths

### Storage System Issues

**Issue**: Items disappear when inserted
```
Items vanish without being stored
```

**Solution**:
1. Check `StorageSlot.set()` implementation
2. Verify `insertItem()` returns remainder correctly
3. Enable debug logging to trace item flow
4. Check storage capacity is properly set

**Issue**: Storage capacity is zero
```
Storage shows 0/0 capacity
```

**Solution**:
1. Verify multiblock scanning is working
2. Check storage blocks extend `BlockStorage` correctly
3. Ensure `scanMultiblock()` is called on placement
4. Verify `getCapacity()` returns correct values

**Issue**: Multiblock not detected
```
Storage blocks don't connect to core
```

**Solution**:
1. Check blocks are adjacent (6-directional connectivity)
2. Verify all blocks extend `StorageMultiblock`
3. Check `attemptMultiblock()` is called on block placement
4. Enable debug logging for multiblock scanning

## GUI Issues

### Layout Problems

**Issue**: Text overlaps with slots
```
Labels render in middle of inventory grid
```

**Solution**:
1. Check `titleLabelY` and `inventoryLabelY` positioning
2. Verify GUI height matches slot layout
3. Update label positions in screen constructor
4. Test with different GUI scale settings

**Issue**: Slots positioned incorrectly
```
Inventory slots don't align with GUI texture
```

**Solution**:
1. Verify slot positioning formulas: `8 + column * 18`
2. Check GUI texture dimensions match expected size
3. Ensure slot coordinates match texture layout
4. Test with GUI debug overlay enabled

### Interaction Issues

**Issue**: Shift-click doesn't work
```
Items don't transfer when shift-clicking
```

**Solution**:
1. Check `quickMoveStack()` implementation
2. Verify slot index ranges are correct
3. Ensure `moveItemStackTo()` parameters are valid
4. Check slot types support the operation

**Issue**: Drag & drop not working
```
Items don't insert when dragged to storage slots
```

**Solution**:
1. Verify `StorageSlot.mayPlace()` returns true
2. Check `StorageSlot.set()` handles insertion
3. Ensure storage core block entity is accessible
4. Verify container is properly initialized

## Networking Issues

### Synchronization Problems

**Issue**: Client and server out of sync
```
Items appear on client but not on server
```

**Solution**:
1. Check packet registration in `EZNetwork`
2. Verify packet serialization/deserialization
3. Ensure sync packets are sent after storage changes
4. Check packet handling on both sides

**Issue**: Packet serialization errors
```
Failed to encode/decode packet data
```

**Solution**:
1. Verify `StreamCodec` implementation
2. Check data types match between encode/decode
3. Ensure all packet fields are properly serialized
4. Test with simple packet data first

## JEI Integration Issues

### Recipe Transfer Problems

**Issue**: JEI recipe transfer not working
```
Clicking recipes doesn't fill crafting grid
```

**Solution**:
1. Check JEI plugin registration
2. Verify recipe transfer handler implementation
3. Ensure crafting menu is properly registered
4. Check slot indices match expected layout

**Issue**: Items not visible in JEI
```
EZStorage items don't appear in ingredient list
```

**Solution**:
1. Verify creative tab registration
2. Check items are added to creative tab
3. Ensure item registration is complete
4. Verify JEI can access item registry

## Performance Issues

### Memory Problems

**Issue**: High memory usage
```
OutOfMemoryError during gameplay
```

**Solution**:
1. Check for memory leaks in storage system
2. Verify proper cleanup of block entities
3. Monitor packet frequency and size
4. Use profiler to identify memory hotspots

**Issue**: Slow multiblock scanning
```
Game freezes when placing storage blocks
```

**Solution**:
1. Optimize multiblock scanning algorithm
2. Add limits to scan depth/breadth
3. Cache scan results when possible
4. Consider async scanning for large networks

## Debugging Techniques

### Enable Debug Logging

Add to `log4j2.xml`:
```xml
<Logger level="DEBUG" name="com.zerofall.ezstorage"/>
```

Or use JVM argument:
```
-Dlog4j.logger.com.zerofall.ezstorage=DEBUG
```

### Common Log Patterns

**Storage Operations**:
```
[DEBUG] EZInventory.insertItem: minecraft:cobblestone x64, current capacity: 0/50000
[DEBUG] Creating new storage entry for minecraft:cobblestone x64
```

**Multiblock Scanning**:
```
[INFO] Scanning multiblock at BlockPos{x=100, y=64, z=200}
[DEBUG] Found storage block at BlockPos{x=101, y=64, z=200} with capacity 10000
[INFO] Multiblock scan complete. Found 5 blocks, total capacity: 50000
```

**GUI Operations**:
```
[DEBUG] StorageSlot.set: minecraft:iron_ingot x32
[DEBUG] quickMoveStack: slot 27
```

### Development Tools

**Recommended IDE Settings**:
- Enable auto-import for NeoForge classes
- Set up run configurations for client/server
- Configure debugger for mod development

**Useful Gradle Tasks**:
```bash
./gradlew build          # Full build
./gradlew runClient      # Test client
./gradlew runServer      # Test server
./gradlew test           # Run unit tests
./gradlew clean          # Clean build artifacts
```

## Getting Help

### Log Analysis

When reporting issues, include:
1. Full error stack trace
2. Relevant log entries with timestamps
3. Steps to reproduce the issue
4. Minecraft/NeoForge/mod versions
5. System information (OS, Java version)

### Useful Information

**Version Information**:
```
Minecraft: 1.21.1
NeoForge: 21.1.77
EZStorage: 2.5.0
Java: 21
Gradle: 8.10.2
```

**System Information**:
```
OS: [Operating System]
Memory: [Available RAM]
Graphics: [GPU Information]
```

### Community Resources

- **NeoForge Documentation**: https://docs.neoforged.net/
- **Minecraft Wiki**: https://minecraft.wiki/
- **ModDevGradle**: https://github.com/neoforged/ModDevGradle
- **JEI Documentation**: https://github.com/mezz/JustEnoughItems

## Prevention Tips

### Best Practices

1. **Regular Testing**: Test changes frequently during development
2. **Version Control**: Commit working states before major changes
3. **Logging**: Add comprehensive logging for debugging
4. **Documentation**: Keep code well-documented
5. **Error Handling**: Implement proper error handling and validation

### Code Quality

1. **Null Checks**: Always check for null values
2. **Bounds Checking**: Validate array/list indices
3. **Resource Cleanup**: Properly dispose of resources
4. **Thread Safety**: Consider thread safety for client/server code
5. **Performance**: Profile and optimize critical paths

### Testing Strategy

1. **Unit Tests**: Test individual components
2. **Integration Tests**: Test component interactions
3. **Manual Testing**: Test user workflows
4. **Edge Cases**: Test boundary conditions
5. **Regression Testing**: Verify fixes don't break existing functionality

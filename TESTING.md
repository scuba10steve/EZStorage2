# Steve's Simple Storage - Test Suite

## Test Coverage

Unit tests live in the `common` module and test pure Java classes without Minecraft runtime dependencies.

### Test Classes

1. **RefStringsTest** - Tests mod constants
   - `testModId()` - Validates RefStrings.MODID value
   - `testModName()` - Validates RefStrings.NAME value
   - `testModVersion()` - Validates RefStrings.VERSION value

2. **SortModeTest** - Tests sort mode enum behavior
   - Validates display names, rotation, and comparator logic

3. **ExtractListModeTest** - Tests extract list mode enum behavior
   - Validates display names, descriptions, and rotation

4. **SecurityBoxTest** - Tests security box data structures

### Running Tests

```bash
# Run common module unit tests
./gradlew :common:test

# Run tests with clean build
./gradlew :common:clean :common:test

# View test report
open common/build/reports/tests/test/index.html
```

### Test Results

- **Total Tests**: 4 test classes
- **Passing**: All
- **Location**: `common/src/test/java/`

### Notes

- Tests live in the `common` module alongside the platform-agnostic code they test
- Only pure Java classes without Minecraft runtime dependencies can be unit tested
- Registration classes (in `neoforge` module) depend on NeoForge APIs and cannot be unit tested
- NeoForge game tests (in `neoforge/.../gametest/`) provide integration-level testing

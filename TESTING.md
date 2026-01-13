# EZStorage 2 - Test Suite

## Test Coverage

This project includes minimal unit tests that actually test our source code.

### Test Classes

1. **RefStringsTest** - Tests mod constants from our actual RefStrings class
   - `testModId()` - Validates RefStrings.MODID value
   - `testModName()` - Validates RefStrings.NAME value  
   - `testModVersion()` - Validates RefStrings.VERSION value

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with clean build
./gradlew clean test

# View test report
open build/reports/tests/test/index.html
```

### Test Results

- **Total Tests**: 3
- **Passing**: 3
- **Failing**: 0
- **Coverage**: Actual source code constants

### Notes

- Tests only include those that test actual source code
- RefStrings is the only class we can test without Minecraft dependencies
- Registration classes cannot be tested due to ModDevGradle limitations
- Integration tests would require a full mod environment setup

### Why So Few Tests?

The ModDevGradle plugin makes Minecraft classes unavailable during test compilation, preventing us from testing:
- Registration classes (depend on DeferredRegister)
- Block/Item classes (depend on Minecraft base classes)
- Block entities (depend on Minecraft BlockEntity)

Only pure Java classes without Minecraft dependencies can be unit tested.

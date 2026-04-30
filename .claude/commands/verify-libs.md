# Verify library dependencies

1. Run ./gradlew dependencies and analyze it. Exclude unused or legacy libraries and modules (exclude(group = "*", module = "*"))
2. fix build.gradle.kts and other project files if necessary. Source files (*.kt) must remain unchanged
3. Run and verify all test. Add necessary updates in project files if necessary
4. Verify project (./sh/other/bc), fix that if necessary


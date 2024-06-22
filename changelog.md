# Changelog

## Version 0.2.0 - 2024-06-13

### New Features
 - **Shredder Capabilities**
  - Introduced the new `CreateShredderBlock` for some more kinetic interactions
  - Added functionality to shred various items.
  - Integrated with the `Create` mod to utilize rotational power for shredding operations.
  - Configurable shredding recipes to allow for custom item processing.

- Added `KubeJS` support for enhanced scripting capabilities.

### Improvements
- Updated to Minecraft version `1.20.1`.
- Updated to Forge version `47.2.0`.
- Improved block registration process to ensure proper initialization.
- Set render layers using `BlockRenderLayerMap` for better compatibility with newer Minecraft versions.

### Bug Fixes
- Fixed a `NullPointerException` caused by a missing registry entry for `amazingtrading:shop`.
- Ensured that the `SHOP` block is registered correctly and its render layer is set during the client setup phase.

### Technical Changes
- Updated `gradle.properties` to reflect the new Minecraft and Forge versions.
- Updated `build.gradle` to use the latest mappings and dependencies.
- Updated `META-INF/mods.toml` to reflect the new mod version and dependencies.

### Documentation
- Updated the changelog to include recent changes and improvements.

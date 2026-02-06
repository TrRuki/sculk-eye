You can install this mod on [Modrinth](https://modrinth.com/mod/sculk-eye)

This mod adds a new functional block:
# Sculk Eye Block

This block emits a redstone signal depending on the amount of set entities detected in a set radius
![example usage image](https://cdn.modrinth.com/data/cached_images/3e55b1d28c0ef17825863564d4b4f3ea2c87c3c0.jpeg)

#### Parameters

- Enitiy Mode: Player, Any mob (not player), Friendly, Hostile, Custom (specified in custom entity type param)
- Radius: 0-100
- Custom entity type: Entity type, example: "minecraft:creeper" or "creeper"

To change these parameters you can right click on the block and it will open a GUI

![GUI example image](https://cdn.modrinth.com/data/cached_images/5b1e2c9893f4f9a4411dd6661f96fae0936ea8c6.jpeg)

## Additional info

This mod uses Fabric mod loader & Fabric API. So the Fabric API mod is required for this mod to work properly.

Recipe:

![recipe image](https://cdn.modrinth.com/data/cached_images/88e426c60511aa47702da0879afde71c08521623.png)


## Building

1. Clone the repo
```bash
clone https://github.com/TrRuki/sculk-eye.git
cd sculk-eye
```
2. Build 
```bash
./gradlew
```
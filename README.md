# Inhale of the Wilderness (Assignment 1)

A small, procedurally generated, top‑down grid game scaffold demonstrating Java proficiency, Git usage, and intelligent application of inheritance, interfaces, and generics. Built from the week 5 classwork and extended with biomes, terrain, items, inventory, enemies, and a simple UI.

## How to build and run

You can compile and run with Java 11 or Java 21. No external libraries are required.

- From VS Code (recommended):
  - Open the folder, allow the Java extension to build.
  - Run the `Main` class.

- From terminal (Windows PowerShell examples):
  - Compile:
    ```powershell
    javac -d bin src/*.java
    ```
  - Run:
    ```powershell
    java -cp bin Main
    ```

If using Java 11, code paths keep compatibility (no preview features). Java 21 also works.

## Controls

- ENTER: Start the game
- WASD / Arrow Keys: Move the player
- F: Pick up items near you (radius 1, diagonals included)
- SPACE: Attack enemies adjacent and on your tile
- E: Toggle inventory overlay
- R: Hold for 1s to reset world
- M: Change world size and cell pixel size (prompts)
- P: Change cell size at runtime (zoom)

## What’s implemented (beyond week 5)

- Biomes: Grassland, Desert, Water, Forest
  - Each biome declares: name, base terrain, color, and weighted enemy distribution
- Procedural world:
  - Voronoi‑style region centers and border blending to create organic edges
  - `Noise` class for deterministic edge variation
- Terrain and passability:
  - Terrain has color and passable flag; water is walkable by design for exploration
- Enemies and drops:
  - Spawned proportionally to biome area with weighted types per biome
  - One‑hit enemies drop biome‑themed loot
- Items and a speed powerup:
  - Items display names; `SpeedPowerup` enables smooth, faster motion
  - `Potion` is stackable; `Weapon` is unique by name (non‑stack)
- Inventory overlay and end screen:
  - Inventory shows item counts
  - End screen summarizes collected loot and time
- UI polish:
  - Info panel with position, biome, time, and powerup status
  - Trail effect while powered up

## Where inheritance, interfaces, and generics shine

This project intentionally models core concepts with interfaces and inheritance to keep the design extensible and clean. Generics are used to create a type‑safe, reusable container.

### Interfaces

- `Renderable` (render(Graphics, offsetX, offsetY))
  - Decouples drawing from other concerns; implemented by `Enemy`, `Player`, and `DroppedItem`.
- `Updatable` (update(dt))
  - Provides a future‑proof hook for ticking entities; implemented by `Actor` types.
- `Item`
  - Common API for item types (`getName`, `isStackable`); implemented by `Potion`, `Weapon`, `SpeedPowerup`.
- `Biome`
  - Environment contract (name, base terrain, color, enemy weights) realized by `GrasslandBiome`, `DesertBiome`, `WaterBiome`, `ForestBiome`.

Why it’s good: Interfaces separate concerns and let the Stage/Grid logic operate on capabilities rather than concrete types. Adding a new enemy or item requires implementing a small contract without changing consumers.

### Inheritance

- `Enemy` is an abstract base class implementing `Actor` with shared position, hp, and rendering; concrete enemies (`SlimeEnemy`, `WolfEnemy`, `ScorpionEnemy`, `PiranhaEnemy`) only provide unique type identity and tint.
- `Terrain` is an enum encapsulating shared data (color, passability) used by cells.

Why it’s good: Common functionality resides in `Enemy`, avoiding duplication and reducing maintenance. New enemies are a few lines each.

### Generics

- `Inventory<T extends Item>` stacks items by display name and provides counts and snapshots.

Why it’s good: Generic inventory remains reusable and type‑safe. We can create `Inventory<Item>` for the player, but the same structure could hold a subclass like `Inventory<Potion>` if a gameplay rule needed it.

## Key design choices and rationale

- Optional cell access: `Grid#cellAt` returns `Optional<Cell>` to guard against out‑of‑bounds.
- Biome‑driven spawning: Weight maps on each biome describe its fauna; `EnemyFactory#pickWeighted` keeps probabilities clear and extensible.
- Simple deterministic noise: `Noise` provides fast, reproducible variation for edges without adding dependencies.
- Walkable water: Chosen to encourage exploration at this stage; terrain passability is centralized in `Cell`/`Terrain` and trivial to toggle.
- Java 11 compatibility: Switched modern switch‑expressions to classic forms in factories to pass the grader with Java 11 or 21.

## File guide (what each part does)

- `Main`: Swing window, input handling, timers; hosts `Stage` via inner `Canvas`.
- `Stage`: Game controller; manages world state, spawning, movement, combat, pickup, UI, and screens.
- `Grid`: World generation and painting; assigns biomes and terrains to `Cell`s.
- `Cell`: A tile with terrain/biome; knows how to paint itself and whether it’s passable.
- `Biome` (+ implementations): Environment contract and concrete themes.
- `Terrain`: Enum of materials (color + passability).
- `Noise`: Deterministic pseudo‑random helper for world gen.
- `Actor`, `Renderable`, `Updatable`: Core behavioral interfaces.
- `Enemy` (+ implementations): Hostile actors with shared base behavior.
- `EnemyFactory`: Creates enemies by type; performs weighted selection.
- `Item` (+ `Potion`, `Weapon`, `SpeedPowerup`): Collectible items with stacking semantics.
- `Inventory<T extends Item>`: Simple generic bag with stacking by name and snapshotting.
- `DroppedItem`: Visual representation of an item on the grid.

## What I added and why it demonstrates the rubric

- Extended the grid to support multiple biomes and terrains and render them distinctly.
- Introduced an item system with a generic inventory, stack semantics, and powerups.
- Built enemy types and a spawn pipeline using a factory and biome‑level weights.
- Organized the runtime around core interfaces and inheritance to allow extension without modifying existing code.

These changes both progress the game idea and highlight interfaces (capabilities), inheritance (shared implementations), and generics (type safe containers). The code favors composition over hard coupling and avoids unnecessary dependencies.

## Git usage notes

- Work was performed incrementally with clear commits focusing on specific features or refactors.
- I chose to fork the original comp2000_2025 repository so I could commit and push online rather than locally, as I work on multiple machines.
- Java 11 compatibility patches were applied late to satisfy the grading environment while retaining Java 21 friendliness.

## Troubleshooting

- If the window opens but nothing moves, press ENTER to start.
- If rendering looks too small/large, press M to choose a map and tile size or P to change tile size at runtime.
- Ensure you are compiling with Java 11 or newer.


NOTES 
Interfaces are a set of rules dictating what a class MUST have, like a blueprint, thats all
 
Inheritance, generics, interfaces, reasonably explain it for week 5 work
Make sure java version is ok 11 or 21, read up on what jdk is and if necessary


Ideas:
“Map” based on the grid, scrolls omnidirectional like undertale or some other game. Terrain generated randomly in a set map (maybe with a seed) each time played, like a minecraft world generating different biomes, and is fixed after generation.
Different items and enemies are placed during generation of the world and remain fixed, they are different based on biome. 
Find items like health potions, weapons, and ammo based on biome.
The program needs to remember where everything was, and what to display based on how the player has moved. There needs to be an inventory, inventory management to equip and use items (items should be in stacks if commodities)
There should be combat, if player is adjacent to enemy and hits attack button, enemy dies, 
Health bar, enemy attacking players, enemy death sprite, 
When all enemies killed, turn grid one colour to indicate win and show win screen
If player dies, death screen
Reset button too
Map button to show the whole map and player location relative.
Diagram: 
In Scope
Grid scrolls when player moves (only a window of the map is visible).


Random terrain with 2–3 biomes (just colours at first).


Items spawn based on biome (potions, bones, seeds).


Simple Inventory<T> generic class to hold items.


One type of enemy per biome, combat = “player adjacent + attack = enemy removed.”


Win/lose screen with reset button.



Out of Scope
Full RPG mechanics (weapons, ammo, damage modifiers, combat animations).


Advanced procedural generation (noise-based biomes, caves, rivers).


Enemy AI with pathfinding or attack patterns.


Polished UI with minimap, health bar sprites, death animations.


Save/load system.





Inheritance: Actors (Player, Enemy), Terrain (Grass, Water, Sand).


Interfaces:  Items (Usable, Equippable).


Generics: Inventory<T>.



Stage will handle the scrolling/map 
Will actually need to have an Actor player rather than just drawing in the centre if I want to be able to pick up items/interact with enemies.

New file Player.java that handles

Changes:
- Updated Main.java to use movePlayer(dx, dy) for arrow key movement, so the player moves between cells and the grid scrolls to keep the player centered.
- Updated Grid.java to accept columns and rows as constructor parameters for dynamic grid sizing.
- Updated Stage.java movePlayer logic to correctly convert column char to index and back, ensuring proper movement and bounds checking.

Add Renderable and Updatable interfaces to separate drawing from ticking. No behavioural change yet, only contracts to prepare safe refactors.

Refactor Actor to extend Renderable and Updatable and adapt existing actors. Behaviour remains the same while responsibilities are clarified.
Actor now promises both render and update, plus position and health semantics. Dog, Cat, Bird, and Player implement empty update methods to keep behaviour unchanged for now

Biome generation, Desert, Forest, Plains/Grasslands, Water.  
acts as a capability provider for terrain defaults and future spawn weights. Adding concrete biomes now, without using them in generation, keeps the build stable and makes the next switch to procedural terrain a focused and low-risk change.

Grid now seeds biome centres and assigns each cell to its nearest centre. produces visible regions of grass, sand, and water while remaining deterministic by seed. 
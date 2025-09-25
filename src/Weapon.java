// Weapon: simple non-stackable item with a default name.
public class Weapon implements Item { 
    @Override public String getName() { return "Sword"; } 
    @Override public boolean isStackable() { return false; } 
}
   


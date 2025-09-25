// Potion: stackable consumable item.
public class Potion implements Item {
    @Override public String getName() { return "Potion"; }
    @Override public boolean isStackable() { return true; }
}

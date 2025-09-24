public class Potion implements Item {
    private final String id = "potion";
    private final String label = "Health Potion";
    public String id() { return id; }
    public String label() { return label; }
    public String getName() { return "Potion"; }
    public boolean isStackable() { return true; }
}

public class Potion implements Item {
    private final String id = "potion";
    private final String label = "Health Potion";
    public String id() { return id; }
    public String label() { return label; }
    @Override public String getName() { return "Potion"; }
    @Override public boolean isStackable() { return true; }
}

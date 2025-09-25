// Inventory<T>: simple name-grouped bag with stacking by item name.
import java.util.*;

public class Inventory<T extends Item> {
    // Items are grouped by display name to allow stacking (e.g., multiple Potions).
    private final Map<String, List<T>> byName = new HashMap<>();

    // Add an item to the bag and stack it under its name.
    public void add(T item) {
        byName.putIfAbsent(item.getName(), new ArrayList<>());
        byName.get(item.getName()).add(item);
    }

    // Count how many items with the given name are present.
    public int count(String name) {
        List<T> list = byName.get(name);
        return list == null ? 0 : list.size();
    }

    // View utilities
    public List<T> getAll(String name) { return byName.getOrDefault(name, List.of()); }
    public Set<String> names() { return byName.keySet(); }

    // Debug helper for console.
    public void debugPrint() {
        for (var e : byName.entrySet()) {
            System.out.println(e.getKey() + " x" + e.getValue().size());
        }
    }

    // Returns a map of item names to counts.
    public Map<String, Integer> snapshot() {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (var e : byName.entrySet()) {
            out.put(e.getKey(), e.getValue().size());
        }
        return out;
    }

}
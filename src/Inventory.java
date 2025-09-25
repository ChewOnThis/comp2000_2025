import java.util.*;

public class Inventory<T extends Item> {
    private final Map<String, List<T>> byName = new HashMap<>();

    public void add(T item) {
        byName.putIfAbsent(item.getName(), new ArrayList<>());
        byName.get(item.getName()).add(item);
    }

    public int count(String name) {
        List<T> list = byName.get(name);
        return list == null ? 0 : list.size();
    }

    public List<T> getAll(String name) { return byName.getOrDefault(name, List.of()); }
    public Set<String> names() { return byName.keySet(); }

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
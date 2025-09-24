import java.util.*;

public class Inventory<T extends Item> {
    private final Map<String, List<T>> slots = new LinkedHashMap<>();

    public void add(T item) {
        slots.computeIfAbsent(item.getName(), k -> new ArrayList<>()).add(item);
    }

    public Optional<T> takeOne(String name) {
        List<T> list = slots.get(name);
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.remove(list.size() - 1));
    }

    public int count(String name) {
        var l = slots.get(name);
        return l == null ? 0 : l.size();
    }

    public Map<String, Integer> snapshot() {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (var e : slots.entrySet()) out.put(e.getKey(), e.getValue().size());
        return out;
    }

   
    public Set<String> names() { return new LinkedHashSet<>(slots.keySet()); }
    public List<T> getAll(String name) { return Collections.unmodifiableList(slots.getOrDefault(name, List.of())); }
    public void debugPrint() { for (var e : slots.entrySet()) System.out.println(e.getKey() + " x" + e.getValue().size()); }
}

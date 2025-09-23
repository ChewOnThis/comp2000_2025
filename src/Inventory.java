import java.util.*;

public class Inventory<T extends Item> {
    private final Map<String, List<T>> slots = new LinkedHashMap<>();

    public void add(T item) { slots.computeIfAbsent(item.id(), k -> new ArrayList<>()).add(item); }
    public Optional<T> takeOne(String id) {
        List<T> list = slots.get(id);
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.remove(list.size() - 1));
    }
    public int count(String id) { var l = slots.get(id); return l == null ? 0 : l.size(); }
    public Map<String, Integer> snapshot() {
        Map<String, Integer> out = new LinkedHashMap<>();
        for (var e : slots.entrySet()) out.put(e.getKey(), e.getValue().size());
        return out;
    }
}

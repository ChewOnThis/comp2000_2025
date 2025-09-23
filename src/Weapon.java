public interface Weapon extends Item {
    int damage();
    default String label() { return "Weapon"; }
}

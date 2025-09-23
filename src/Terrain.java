public enum Terrain {
    GRASS(java.awt.Color.GREEN),
    SAND(java.awt.Color.YELLOW),
    WATER(java.awt.Color.CYAN);

    private final java.awt.Color colour;
    Terrain(java.awt.Color c) { this.colour = c; }
    public java.awt.Color getColour() { return colour; }
}

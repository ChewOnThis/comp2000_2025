public final class Noise {
    // Used for biome blending and terrain variation.
    private final int seed;
    public Noise(int seed) { this.seed = seed; }
    // Hash function! Psuedo-random integer
    private int hash(int x, int y) {
        int h = seed ^ (x * 374761393) ^ (y * 668265263);
        h = (h ^ (h >>> 13)) * 1274126177;
        return h ^ (h >>> 16);
    }
    public double value(int x, int y) {
        int h = hash(x, y);
        return (h & 0x7fffffff) / (double)0x7fffffff;
    }
}

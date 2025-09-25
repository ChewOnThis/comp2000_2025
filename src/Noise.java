public final class Noise {
    private final int seed;
    public Noise(int seed) { this.seed = seed; }

    private int hash(int x, int y) {
        int h = seed;
        h ^= x * 374761393;
        h ^= y * 668265263;
        h = (h ^ (h >>> 13)) * 1274126177;
        return h ^ (h >>> 16);
    }

    public double value(int x, int y) {
        int h = hash(x, y);
        return (h & 0x7fffffff) / (double)0x80000000L;
    }
}

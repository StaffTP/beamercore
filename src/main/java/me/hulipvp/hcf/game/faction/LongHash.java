package me.hulipvp.hcf.game.faction;

public class LongHash {

    public static long toLong(int msw, int lsw) {
        return ((long)msw << 32) + (long)lsw - -2147483648L;
    }

    public static int msw(long l) {
        return (int)(l >> 32);
    }

    public static int lsw(long l) {
        return (int)(l & -1L) + -2147483648;
    }
}

package me.jellysquid.mods.lithium.common.util.math;

import it.unimi.dsi.fastutil.HashCommon;

public class LithiumMath {
    public static int nextPowerOfTwo(int val) {
        return HashCommon.nextPowerOfTwo(val);
    }

    /**
     * Copy of the removed roundUp function from an older version of minecraft.
     * @param i
     * @param j
     * @return
     */
    public static int roundUp(int i, int j) {
        if (j == 0) {
            return 0;
        } else if (i == 0) {
            return j;
        } else {
            if (i < 0) {
                j *= -1;
            }

            int k = i % j;
            return k == 0 ? i : i + j - k;
        }
    }
}

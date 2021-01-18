package me.jellysquid.mods.lithium.common.gen;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class CompactNoiseWeightLUT {

    // Use a ~7x smaller array to reduce memory weight and hopefully make it fit into the cache.
    private static final int[] NOISE_WEIGHT_TABLE = Util.make(new int[13 * 13 * 12], (array) -> {
        int idx = 0;
        for (int x = 0; x < 13; x++) {
            for (int z = 0; z < 13; z++) {
                for (int y = 0; y < 12; y++) {
                    array[idx++] = Float.floatToRawIntBits((float) calculateNoiseWeight(x, y, z));
                }
            }
        }
    });


    public static double getNoiseWeight(int x, int y, int z) {
        int flag = y & Integer.MIN_VALUE;
        if (y < 0) {
            y = ~y;
        }
        assert y >= 0;
        if (x < -12 || x >= 12 || y >= 12 || z < -12 || z >= 12) {
            return 0.0D;
        }
        return Float.intBitsToFloat(NOISE_WEIGHT_TABLE[Math.abs(x) * 13 * 12 + Math.abs(z) * 12 + y] ^ flag);
    }

    public static double getNoiseWeightPositive(int x, int y, int z) {
        assert x >= 0 && z >= 0;
        int flag = y & Integer.MIN_VALUE;
        if (y < 0) {
            y = ~y;
        }
        assert y >= 0;
        if (x >= 12 || y >= 12 || z >= 12) {
            return 0.0D;
        }
        return Float.intBitsToFloat(NOISE_WEIGHT_TABLE[x * 13 * 12 + z * 12 + y] ^ flag);
    }

    // We could just make this function accessible
    // [VanillaCopy]
    private static double calculateNoiseWeight(int x, int y, int z) {
        double d = (double) (x * x + z * z);
        double e = (double) y + 0.5D;
        double f = e * e;
        double g = Math.pow(2.718281828459045D, -(f / 16.0D + d / 16.0D));
        double h = -e * MathHelper.fastInverseSqrt(f / 2.0D + d / 2.0D) / 2.0D;
        return h * g;
    }

    static {
        /*
        // verify if all noise weights are equal
        for (int x = -13; x <= 13; x++) {
            for (int y = -13; y <= 13; y++) {
                for (int z = -13; z <= 13; z++) {
                    double orig = getNoiseWeight(x, y, z);
                    double ours = getNoiseWeightFast(x, y, z);
                    // only call if both x and z are positive
                    double oursPositive = (x >= 0 && z >= 0) ? getNoiseWeightPositive(x, y, z) : ours;

                    int flag = 0;
                    if (orig != ours) {
                        flag |= 1;
                    }
                    if (orig != oursPositive) {
                        flag |= 2;
                    }
                    if (ours != oursPositive) {
                        flag |= 4;
                    }
                    switch (flag) {
                        case 0:
                            continue;
                        case 3:
                            throw new RuntimeException("Both our noise weight implementations gave the same wrong result for (" + x + "," + y + "," + z + ")! (expected: " + orig + ", got: " + ours + ")");
                        case 5:
                            throw new RuntimeException("Our general noise weight implementations gave the same wrong result for (" + x + "," + y + "," + z + ")! (expected: " + orig + ", got: " + ours + ")");
                        case 6:
                            throw new RuntimeException("Our positive noise weight implementations gave the same wrong result for (" + x + "," + y + "," + z + ")! (expected: " + orig + ", got: " + oursPositive + ")");
                        case 7:
                            throw new RuntimeException("Both noise weight implementations gave different wrong results for (" + x + "," + y + "," + z + ")! (expected: " + orig + ", got normal: " + ours + " and positive: " +oursPositive + ")");
                        default:
                            throw new RuntimeException("Our general noise weight gave an invalid flag for (" + x + "," + y + "," + z + "): " + flag);
                    }
                }
            }
        }
         */
    }
}

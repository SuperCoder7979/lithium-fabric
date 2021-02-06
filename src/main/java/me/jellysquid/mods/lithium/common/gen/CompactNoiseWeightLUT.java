package me.jellysquid.mods.lithium.common.gen;

import net.minecraft.util.Util;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

/**
 * Replaces the noise weight table used to offset the density of terrain near jigsaw structures with a cache that is
 * around 7 time smaller by reducing duplicated elements. The array represents a sphere of values to be added to the
 * noise values around a structure.
 *
 * The values in the array are largest close to it's origin (12, 12, 12 in array space) and gradually gets smaller
 * as the distance increases. The values below the origin, from 0 to 12 in the array, are positive to ensure terrain
 * under structures is solid. The values at the origin or above are negative, to ensure the structure has enough space
 * to generate without touching terrain that may be above it.
 *
 * Knowing that it is a sphere, we can divide it into octants and only store one, knowing that the store octant can
 * represent the whole array. Even though the top half of the sphere is negative and the bottom half is positive, it is
 * still symmetrical. This brings down the array size from 24 * 24 * 24 to 13 * 12 * 13. Unfortunately the values are
 * not positive on the horizontal axes so one extra value must be stored, which is why the X and Z axes have a size of
 * 13. By applying these optimizations, the table can be reduced from 13824 elements (~55KB) to 2028 elements (~8KB). As
 * this array is addressed extensively during chunk generation in villages areas, this may play an important role in
 * keeping chunk generation times low.
 *
 * A runtime check is included to ensure the values of the original array match our version, preventing chunk generation
 * errors.
 *
 * @author Kroppeb       Primary author of lookup table and tests
 * @author SuperCoder79  Original implementation and documentation
 */
public class CompactNoiseWeightLUT {
    // Use a ~7x smaller array to reduce memory weight and hopefully make it fit into the cache.
    private static final int[] NOISE_WEIGHT_TABLE = Util.make(new int[13 * 13 * 12], (array) -> {
        int idx = 0;
        for (int x = 0; x < 13; x++) {
            for (int z = 0; z < 13; z++) {
                for (int y = 0; y < 12; y++) {
                    array[idx++] = Float.floatToRawIntBits((float) NoiseChunkGenerator.calculateNoiseWeight(x, y, z));
                }
            }
        }
    });


    public static double getNoiseWeight(int x, int y, int z) {
        // Set flag when positive to make the result negative.
        // A positive y value causes negative results to carve air into the surrounding terrain, ensuring the structure has enough space to spawn.
        int flag = y & Integer.MIN_VALUE;

        // Flip the bits when y is negative, making it positive and reducing it by 1.
        // Fast representation of y = -1 - y.
        if (y < 0) {
            y = ~y;
        }

        // Fail fast when out of bounds.
        if (y >= 12 || x < -12 || x >= 12 || z < -12 || z >= 12) {
            return 0.0;
        }

        // Get the absolute value of the x and z and index the array to find the value.
        return Float.intBitsToFloat(NOISE_WEIGHT_TABLE[Math.abs(x) * 13 * 12 + Math.abs(z) * 12 + y] ^ flag);
    }

    static {
        // verify if all noise weights are equal
        for (int x = -13; x <= 13; x++) {
            for (int y = -13; y <= 13; y++) {
                for (int z = -13; z <= 13; z++) {
                    double original = NoiseChunkGenerator.getNoiseWeight(x, y, z);
                    double ours = getNoiseWeight(x, y, z);

                    if (ours != original) {
                        throw new IllegalArgumentException("Noise Weight LUT error at (" + x + ", " + y + ", " + z + ")! Expected " + original + " but found " + ours);
                    }
                }
            }
        }

    }
}

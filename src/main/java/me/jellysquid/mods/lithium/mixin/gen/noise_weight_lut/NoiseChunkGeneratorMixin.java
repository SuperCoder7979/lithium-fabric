package me.jellysquid.mods.lithium.mixin.gen.noise_weight_lut;

import me.jellysquid.mods.lithium.common.gen.CompactNoiseWeightLUT;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {

    /**
     * Uses a smaller LUT (approx 6.8 times smaller)
     *
     * @author Kroppeb
     */
    @Overwrite
    private static double getNoiseWeight(int x, int y, int z) {
        return CompactNoiseWeightLUT.getNoiseWeight(x, y, z);
    }
}
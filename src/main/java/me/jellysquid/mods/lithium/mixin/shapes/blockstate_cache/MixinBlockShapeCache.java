package me.jellysquid.mods.lithium.mixin.shapes.blockstate_cache;

import me.jellysquid.mods.lithium.common.block.BlockShapeCacheExtended;
import me.jellysquid.mods.lithium.common.block.BlockShapeHelper;
import me.jellysquid.mods.lithium.common.util.BitUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.EmptyBlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extends the ShapeCache to contain some additional properties as to avoid expensive computation from some Redstone
 * components which look to see if a block's shape can support it. This prompted an issue to be opened on the Mojang
 * issue tracker, which contains some additional information: https://bugs.mojang.com/browse/MC-174568
 */
@Mixin(BlockState.ShapeCache.class)
public class MixinBlockShapeCache implements BlockShapeCacheExtended {
    private static final Direction[] DIRECTIONS = Direction.values();

    private byte sideCoversSmallSquare;
    private byte sideCoversMediumSquare;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(BlockState state, CallbackInfo ci) {
        VoxelShape shape = state.getCollisionShape(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);

        // If the shape is a full cube, it can always support another component
        boolean fullCube = !VoxelShapes.matchesAnywhere(shape, VoxelShapes.fullCube(), BooleanBiFunction.NOT_SAME);

        for (Direction dir : DIRECTIONS) {
            VoxelShape face = shape.getFace(dir);

            this.sideCoversSmallSquare |= BitUtil.bit(dir.ordinal(), fullCube || BlockShapeHelper.sideCoversSquare(face, BlockShapeHelper.SOLID_SMALL_SQUARE_SHAPE));
            this.sideCoversMediumSquare |= BitUtil.bit(dir.ordinal(), fullCube || BlockShapeHelper.sideCoversSquare(face, BlockShapeHelper.SOLID_MEDIUM_SQUARE_SHAPE));
        }
    }

    @Override
    public boolean sideCoversSmallSquare(Direction facing) {
        return BitUtil.contains(this.sideCoversSmallSquare, facing.ordinal());
    }

    @Override
    public boolean sideCoversMediumSquare(Direction facing) {
        return BitUtil.contains(this.sideCoversMediumSquare, facing.ordinal());
    }
}

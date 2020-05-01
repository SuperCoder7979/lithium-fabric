package me.jellysquid.mods.lithium.mixin.alloc.enum_values;

import net.minecraft.entity.EntityCategory;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerChunkManager.class)
public abstract class MixinServerChunkManager {
    
}

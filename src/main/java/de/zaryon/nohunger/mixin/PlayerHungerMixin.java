package de.zaryon.nohunger.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerHungerMixin extends PlayerEntity {

    public PlayerHungerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tickHunger()V", at = @At("HEAD"), cancellable = true)
    protected void onTickHunger(CallbackInfo ci) {
        if (this.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
            ci.cancel();
        }
    }

}
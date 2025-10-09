package de.zaryon.nohunger.mixin;

import com.mojang.authlib.GameProfile;
import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerHungerMixin extends PlayerEntity {

    public PlayerHungerMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "tickHunger()V", at = @At("HEAD"), cancellable = true)
    protected void onTickHunger(CallbackInfo ci) {
        if (this.getWorld().getDifficulty() == Difficulty.PEACEFUL &&
                NoHungerConfig.getInstance().isPeacefulHunger() &&
                (NoHungerConfig.getInstance().getMode() == NoHungerConfig.HungerMode.NORMAL ||
                        NoHungerConfig.getInstance().getMode() == NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE)) {

            ci.cancel();
        }
    }
}
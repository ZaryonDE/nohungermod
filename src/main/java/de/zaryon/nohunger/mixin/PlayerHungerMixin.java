package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerHungerMixin {

    // Erzwingt HARD nur dann, wenn Peaceful + Config aktiv
    @Redirect(
            method = "tickMovement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getDifficulty()Lnet/minecraft/world/Difficulty;")
    )
    private Difficulty redirectDifficultyForPlayer(World instance) {
        if (instance.getDifficulty() == Difficulty.PEACEFUL &&
                NoHungerConfig.getInstance().isPeacefulHunger()) {
            return Difficulty.HARD;
        }
        return instance.getDifficulty();
    }
}

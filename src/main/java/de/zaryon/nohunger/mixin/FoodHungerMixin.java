package de.zaryon.nohunger.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HungerManager.class)
public abstract class FoodHungerMixin {

    @ModifyVariable(
            method ="update(Lnet/minecraft/server/network/ServerPlayerEntity;)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/server/world/ServerWorld;getDifficulty()Lnet/minecraft/world/Difficulty;"
            ),
            index = 3
    )
    private Difficulty hungerManagerDifficultyRemap(Difficulty original) {
        if (original.equals(Difficulty.PEACEFUL)) {
            return Difficulty.NORMAL;
        }
        else {
            return original;
        }
    }
}

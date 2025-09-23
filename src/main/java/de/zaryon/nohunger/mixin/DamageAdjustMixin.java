package de.zaryon.nohunger.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class DamageAdjustMixin {

    @ModifyArg(
            method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"
            )
    )
    private float adjustDamage(DamageSource source, float amount) {
        if (source == DamageSource.CACTUS
                || source == DamageSource.HOT_FLOOR    // Magma block
                || source == DamageSource.IN_FIRE      // Spieler brennt
                || source == DamageSource.ON_FIRE
                || source == DamageSource.FLY_INTO_WALL // Buggy Flug in Wand
                || source == DamageSource.WITHER
        ) {
            return amount * 1.5f; // Schaden auf 150% erhöhen
        }
        return amount; // alle anderen Schaden bleiben gleich
    }
}

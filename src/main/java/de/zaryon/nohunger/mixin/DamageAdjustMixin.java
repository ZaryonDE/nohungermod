package de.zaryon.nohunger.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
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
        if (source.isOf(DamageTypes.CACTUS)                 ||
                source.isOf(DamageTypes.HOT_FLOOR)          ||
                source.isOf(DamageTypes.IN_FIRE)            ||
                source.isOf(DamageTypes.ON_FIRE)            ||
                source.isOf(DamageTypes.FLY_INTO_WALL)      || // Buggy Flug in Wand
                source.isOf(DamageTypes.WITHER)             ||
                source.isOf(DamageTypes.SWEET_BERRY_BUSH)   ||
                source.isOf(DamageTypes.DROWN)

          ) {
            return amount * 1.7f; // Schaden auf 150% erhöhen
        }
        return amount; // alle anderen Schaden bleiben gleich
    }
}
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
        // Schwache Umweltgefahren (0.5 Schaden)
        if (source == DamageSource.CACTUS
                || source == DamageSource.HOT_FLOOR
                || source == DamageSource.IN_FIRE
                || source == DamageSource.WITHER
                || source == DamageSource.SWEET_BERRY_BUSH) {
            return 1.9F;
        }

        // Brennen
        if (source == DamageSource.ON_FIRE) {
            return 2.5F;
        }

        // Ertrinken
        if (source == DamageSource.DROWN) {
            return 3.0F;
        }

        // Erfrieren (Pulverschnee)
        if (source == DamageSource.FREEZE) {
            return 2.0F;
        }

        // Sturzschaden
        if (source == DamageSource.FALL && amount < 2.0F) {
            return 2.0F;
        }

        // Stalagmiten
        if (source == DamageSource.STALAGMITE) {
            return Math.max(amount, 3.0F);
        }

        return amount;
    }
}
package de.zaryon.nohunger.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class DamageAdjustMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void adjustDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        RegistryEntry<DamageType> type = source.getTypeRegistryEntry();
        float newDamage = amount;

        // Schädliche Quellen anpassen
        if (type.matchesKey(DamageTypes.CACTUS)
                || type.matchesKey(DamageTypes.HOT_FLOOR)
                || type.matchesKey(DamageTypes.IN_FIRE)
                || type.matchesKey(DamageTypes.ON_FIRE)
                || type.matchesKey(DamageTypes.WITHER)
                || type.matchesKey(DamageTypes.SWEET_BERRY_BUSH)) {
            newDamage = 1.5F; // leicht erhöht
        }

        if (type.matchesKey(DamageTypes.DROWN)) {
            newDamage = 3.5F; // deutlich erhöht, um Regeneration zu übertreffen
        }

        // Aufruf der Original-Methode mit modifiziertem Schaden
        if (newDamage != amount) {
            cir.setReturnValue(((LivingEntity)(Object)this).damage(source, newDamage));
        }
    }
}

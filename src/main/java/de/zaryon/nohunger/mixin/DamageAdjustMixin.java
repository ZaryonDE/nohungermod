package de.zaryon.nohunger.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class DamageAdjustMixin {

    /**
     * Minecraft 1.20.3 - Fabric - Java 17
     *
     * Modifiziert Schadenswerte direkt, ohne Rekursion.
     * Ziel: Alle Umweltschäden sollen trotz permanenter Regeneration tödlich bleiben.
     */
    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float adjustDamage(float amount, DamageSource source) {
        RegistryEntry<DamageType> type = source.getTypeRegistryEntry();

        // Schwache Umweltgefahren (Vanilla: 0.5 Schaden)
        if (type.matchesKey(DamageTypes.CACTUS)
                || type.matchesKey(DamageTypes.HOT_FLOOR)      // Magmablock
                || type.matchesKey(DamageTypes.IN_FIRE)        // Im Feuer stehen
                || type.matchesKey(DamageTypes.WITHER)         // Witherrose
                || type.matchesKey(DamageTypes.SWEET_BERRY_BUSH)) {
            return 1.9F;
        }

        // Brennen (Vanilla: 0.5/Sekunde über Zeit)
        if (type.matchesKey(DamageTypes.ON_FIRE)) {
            return 2.5F;
        }

        // Ertrinken (Vanilla: 1.0/Sekunde)
        if (type.matchesKey(DamageTypes.DROWN)) {
            return 3.0F;
        }

        // Erfrieren in Pulverschnee (Vanilla: 0.5 alle 2 Sekunden)
        if (type.matchesKey(DamageTypes.FREEZE)) {
            return 2.0F;
        }

        // Niedriger Sturzschaden
        if (type.matchesKey(DamageTypes.FALL)) {
            if (amount < 2.0F) {
                return 2.0F;
            }
        }

        // Stalagmiten
        if (type.matchesKey(DamageTypes.STALAGMITE)) {
            return Math.max(amount, 3.0F);
        }

        return amount;
    }
}
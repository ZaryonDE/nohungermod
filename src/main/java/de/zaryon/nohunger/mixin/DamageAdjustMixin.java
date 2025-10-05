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
     * Modifiziert den Schadenswert DIREKT, bevor er verarbeitet wird.
     * Keine Rekursion, kein erneuter damage()-Aufruf nötig.
     *
     * Ziel: Spieler hat durch volle Hungerleiste permanente Regeneration (1 Herz/4s),
     * aber KEINE Schadensquelle soll dadurch komplett negiert werden.
     * Alle Schäden werden so angepasst, dass sie die Regeneration überwinden.
     */
    @ModifyVariable(
            method = "damage",
            at = @At("HEAD"),
            argsOnly = true
    )
    private float adjustDamage(float amount, DamageSource source) {
        RegistryEntry<DamageType> type = source.getTypeRegistryEntry();

        // Schwache Umweltgefahren (Vanilla: 0.5 Schaden)
        // Problem: Regeneration heilt 1 Herz/4s = 0.25 Herzen/Sekunde
        // → 0.5 Schaden wird sofort geheilt
        if (type.matchesKey(DamageTypes.CACTUS)
                || type.matchesKey(DamageTypes.HOT_FLOOR)      // Magmablock
                || type.matchesKey(DamageTypes.IN_FIRE)        // Im Feuer stehen
                || type.matchesKey(DamageTypes.WITHER)         // Witherrose
                || type.matchesKey(DamageTypes.SWEET_BERRY_BUSH)) {
            return 1.9F; // Überwindet Regeneration deutlich
        }

        // Brennen (Vanilla: 0.5/Sekunde über Zeit)
        // Muss höher sein, da es kontinuierlich ist
        if (type.matchesKey(DamageTypes.ON_FIRE)) {
            return 2.5F; // Deutlich gefährlicher, du stirbst wenn du nicht löschst
        }

        // Ertrinken (Vanilla: 1.0/Sekunde)
        // Bereits gefährlich, aber könnte stärker sein
        if (type.matchesKey(DamageTypes.DROWN)) {
            return 3.0F; // Schneller Tod unter Wasser = Dringlichkeit
        }

        // Erfrieren in Pulverschnee (Vanilla: 0.5 alle 2 Sekunden)
        // Ohne Anpassung: Spieler ist immun gegen Pulverschnee
        if (type.matchesKey(DamageTypes.FREEZE)) {
            return 2.0F; // Macht Pulverschnee-Fallen wieder relevant
        }

        // Niedriger Sturzschaden (unter 2 Herzen)
        // 3-Block-Stürze würden sonst sofort geheilt werden
        if (type.matchesKey(DamageTypes.FALL)) {
            if (amount < 2.0F) {
                return 2.0F; // Minimum 2 Herzen = kleine Stürze bleiben spürbar
            }
            // Hohe Stürze bleiben unverändert (sind ohnehin tödlich)
        }

        // Stalagmiten (Vanilla: variabel, aber oft gering)
        if (type.matchesKey(DamageTypes.STALAGMITE)) {
            return Math.max(amount, 3.0F); // Mindestens 3 Herzen = gefährlich
        }

        // Originalwert für alle anderen Schadensquellen
        // (Lava, Explosionen, Mobs, etc. bleiben unverändert)
        return amount;
    }
}
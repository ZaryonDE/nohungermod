package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    /**
     * Blockiert den Hunger-Effekt nur, wenn die Hungerleiste ausgeschaltet ist
     * und der Modus NICHT NORMAL ist.
     */
    @Inject(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventHungerEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        // Prüfen, ob "this" wirklich ein Spieler ist
        if (!((Object) this instanceof PlayerEntity player)) {
            return; // Wenn kein Spieler → nichts ändern
        }

        // Holen des aktuellen Modus
        NoHungerConfig.HungerMode currentMode = NoHungerConfig.getInstance().getMode();

        // --- NEUE BEDINGUNG ---
        // Nur blocken, wenn Hungerleiste aus UND der Modus weder NORMAL noch SURVIVAL_CAMPFIRE ist.
        if (!NoHungerConfig.getInstance().isShowHungerBar() &&
                currentMode != NoHungerConfig.HungerMode.NORMAL &&
                currentMode != NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE) {

            if (effect.getEffectType() == StatusEffects.HUNGER) {
                // Nur blocken, wenn Spieler tatsächlich Rotten Flesh in der Hand hält
                for (Hand hand : Hand.values()) {
                    ItemStack stack = player.getStackInHand(hand);
                    if (stack.getItem() == Items.ROTTEN_FLESH) {
                        cir.setReturnValue(false); // Effekt blockieren
                        return;
                    }
                }
            }
        }
    }
}
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

    @Inject(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventHungerEffect(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof PlayerEntity player)) {
            return;
        }

        NoHungerConfig.HungerMode currentMode = NoHungerConfig.getInstance().getMode();

        if (!NoHungerConfig.getInstance().isShowHungerBar() &&
                currentMode != NoHungerConfig.HungerMode.NORMAL &&
                currentMode != NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE) {

            if (effect.getEffectType() == StatusEffects.HUNGER) {
                for (Hand hand : Hand.values()) {
                    ItemStack stack = player.getStackInHand(hand);
                    if (stack.getItem() == Items.ROTTEN_FLESH) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}

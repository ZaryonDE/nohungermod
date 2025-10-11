package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityAutoFeederMixin {

    @Inject(
            method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockAutoFeeder(StatusEffectInstance effect, Entity source, CallbackInfoReturnable<Boolean> cir) {
        NoHungerConfig config = NoHungerConfig.getInstance();

        if (config.getMode() == NoHungerConfig.HungerMode.VANILLA_SPECIAL_FOODS ||
                config.getMode() == NoHungerConfig.HungerMode.ALL_FOODS) {

            if (effect.getEffectType() == StatusEffects.HUNGER) {
                if (!(source instanceof ServerPlayerEntity)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}

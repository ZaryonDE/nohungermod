package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class AllFoodsMixin {

    @Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
    private void allowAlwaysEating(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        NoHungerConfig.HungerMode mode = NoHungerConfig.getInstance().getMode();
        ItemStack stack = player.getMainHandStack();

        // ---- ALL_FOODS ----
        if (mode == NoHungerConfig.HungerMode.ALL_FOODS) {
            // Alles essbar: egal ob Item oder Kuchenblock
            cir.setReturnValue(true);
            return;
        }
    }
}
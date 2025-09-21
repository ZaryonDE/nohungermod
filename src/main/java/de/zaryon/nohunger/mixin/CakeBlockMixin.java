package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public class CakeBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void handleCakeEating(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {

        NoHungerConfig.HungerMode mode = NoHungerConfig.getInstance().getMode();
        ItemStack stack = player.getMainHandStack();

        // ---- ALL_FOODS ----
        if (mode == NoHungerConfig.HungerMode.ALL_FOODS) {
            // Kuchen immer essbar
            return;
        }

        // ---- VANILLA_SPECIAL_FOODS ----
        if (mode == NoHungerConfig.HungerMode.VANILLA_SPECIAL_FOODS) {
            boolean isSpecialFood = stack.isFood() && (
                    stack.getItem() == Items.GOLDEN_APPLE ||
                            stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE ||
                            stack.getItem() instanceof SuspiciousStewItem
            );

            if (isSpecialFood) {
                // Kuchen blockieren, wenn Spezialkost in der Hand
                cir.setReturnValue(ActionResult.FAIL);
            }
        }

        // OFF / NO_FOOD -> Vanilla-Verhalten bleibt, nichts ändern
    }
}
package de.zaryon.nohunger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.Blocks;
import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.util.hit.HitResult;

public class NoHungerMod implements ModInitializer {

    private final NoHungerConfig config = NoHungerConfig.getInstance();

    @Override
    public void onInitialize() {

        // Tick Event: Hunger immer auf 20 halten, außer Mod aus
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (config.getMode() == NoHungerConfig.HungerMode.OFF) return;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.getHungerManager().setFoodLevel(20);
                player.getHungerManager().setSaturationLevel(20f);
            }
        });

        // Item-Use Event: Kontrolle der Kategorien + Kuchenlogik
        UseItemCallback.EVENT.register((player, world, hand) -> {
                    ItemStack stack = player.getStackInHand(hand);

                    // Prüfen, ob es Special Food ist
            boolean isSpecialFood = stack.getItem() == Items.GOLDEN_APPLE
                    || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE
                    || stack.getItem() == Items.SUSPICIOUS_STEW;

            // Kuchen-Check
            BlockHitResult hitResult = (BlockHitResult) player.raycast(5.0, 0.0f, false);
            boolean clickedCakeBlock = hitResult.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK
                    && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.CAKE;

            switch (config.getMode()) {
                case VANILLA_SPECIAL_FOODS:
                    // Spezial-Food immer erlaubt
                    if (isSpecialFood) {
                        return ActionResult.PASS;
                    }

                    // Kuchen blockieren, wenn kein Spezial-Food in der Hand
                    if (clickedCakeBlock) {
                        return ActionResult.FAIL;
                    }
                    break;

                case NO_FOOD:
                    // Alte VanillaSpecialFood-Logik: Nur Spezialkost essbar, Kuchen blockieren
                    if (stack.get(DataComponentTypes.FOOD) != null) {
                        return ActionResult.FAIL;
                    }
                case ALL_FOODS:
                    // Alles essbar
                    break;

                case OFF:
                default:
                    // Mod aus, alles normal
                    return ActionResult.PASS;
            }

            return ActionResult.PASS;
        });
    }
}
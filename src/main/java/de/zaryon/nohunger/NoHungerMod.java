package de.zaryon.nohunger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.Blocks;
import de.zaryon.nohunger.config.NoHungerConfig;

public class NoHungerMod implements ModInitializer {

    private final NoHungerConfig config = NoHungerConfig.getInstance();

    @Override
    public void onInitialize() {

        // Tick Event: Hunger immer auf 20 halten, außer Mod aus
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (config.getMode() == NoHungerConfig.HungerMode.OFF) return;

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Immer volle Leiste halten, außer OFF
                player.getHungerManager().setFoodLevel(20);
                player.getHungerManager().setSaturationLevel(20f);
            }
        });

        // Item-Use Event: Kontrolle der Kategorien + Kuchenlogik
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (config.getMode() == NoHungerConfig.HungerMode.OFF) {
                return TypedActionResult.pass(stack);
            }

            // Prüfen ob Spezialkost
            boolean isSpecialFood = stack.isFood() && (
                    stack.getItem() == Items.GOLDEN_APPLE ||
                            stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE ||
                            stack.getItem() instanceof SuspiciousStewItem
            );

            // Kuchen prüfen (auf den der Spieler klickt)
            BlockHitResult hitResult = (BlockHitResult) player.raycast(5.0, 0.0f, false);
            boolean clickedCakeBlock = hitResult.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK
                    && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.CAKE;

            switch (config.getMode()) {
                case NO_FOOD:
                    // Gar nichts essen
                    return TypedActionResult.fail(stack);

                case VANILLA_SPECIAL_FOODS:
                    // Nur Spezialnahrung erlaubt
                    if (!isSpecialFood) return TypedActionResult.fail(stack);

                    // Kuchen blockieren, egal ob Spezialkost in der Hand
                    if (clickedCakeBlock) return TypedActionResult.fail(stack);
                    break;

                case ALL_FOODS:
                    // Alles essbar, inkl. Kuchen
                    // Kein weiteres Eingreifen nötig
                    break;

                default:
                    break;
            }

            return TypedActionResult.pass(stack);
        });
    }
}
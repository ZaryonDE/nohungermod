package de.zaryon.nohunger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;
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
            Item item = stack.getItem();

            // Prüfen, ob es Special Food ist
            boolean isSpecialFood = item == Items.GOLDEN_APPLE
                    || item == Items.ENCHANTED_GOLDEN_APPLE
                    || item instanceof SuspiciousStewItem;

            // Kuchen-Check
            BlockHitResult hitResult = (BlockHitResult) player.raycast(5.0, 0.0f, false);
            boolean clickedCakeBlock = hitResult.getType() == HitResult.Type.BLOCK
                    && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.CAKE;

            switch (config.getMode()) {
                case VANILLA_SPECIAL_FOODS:
                    // Blockiere alles außer Special Food, blockiere Kuchen
                    if (!isSpecialFood || clickedCakeBlock) return TypedActionResult.fail(stack);
                    break;

                case NO_FOOD:
                    // Blockiere alles
                    return TypedActionResult.fail(stack);

                case ALL_FOODS:
                    // Alles essbar
                    break;

                case OFF:
                default:
                    // Mod aus, alles normal
                    return TypedActionResult.pass(stack);
            }

            return TypedActionResult.pass(stack);
        });
    }
}
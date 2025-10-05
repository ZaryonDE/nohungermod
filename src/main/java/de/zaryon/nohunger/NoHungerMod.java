package de.zaryon.nohunger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import de.zaryon.nohunger.config.NoHungerConfig;

public class NoHungerMod implements ModInitializer {

    private final NoHungerConfig config = NoHungerConfig.getInstance();

    @Override
    public void onInitialize() {

        // ----------------------------
        // Hunger-Modi außer NORMAL
        // ----------------------------
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (config.getMode() == NoHungerConfig.HungerMode.NORMAL ||
                    config.getMode() == NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE) {
                return;
            }

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.getHungerManager().setFoodLevel(20);
                player.getHungerManager().setSaturationLevel(20f);
            }
        });

        // ----------------------------
        // Item-Use Event (Items wie Brot, Apfel, etc.)
        // ----------------------------
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (config.getMode() == NoHungerConfig.HungerMode.NORMAL) {
                return TypedActionResult.pass(stack);
            }

            // Spezialkost
            boolean isSpecialFood = stack.get(DataComponentTypes.FOOD) != null && (
                    stack.getItem() == Items.GOLDEN_APPLE ||
                            stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE ||
                            stack.getItem() instanceof SuspiciousStewItem
            );

            // Campfire-Modus
            if (config.getMode() == NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE && stack.get(DataComponentTypes.FOOD) != null) {
                boolean nearCampfire = false;
                BlockPos playerPos = player.getBlockPos();

                for (int x = -3; x <= 3; x++) {
                    for (int y = -3; y <= 3; y++) {
                        for (int z = -3; z <= 3; z++) {
                            BlockPos checkPos = playerPos.add(x, y, z);
                            if (world.getBlockState(checkPos).getBlock() == Blocks.CAMPFIRE ||
                                    world.getBlockState(checkPos).getBlock() == Blocks.SOUL_CAMPFIRE) {
                                nearCampfire = true;
                                break;
                            }
                        }
                        if (nearCampfire) break;
                    }
                    if (nearCampfire) break;
                }

                if (!nearCampfire) {
                    player.sendMessage(
                            net.minecraft.text.Text.literal("Zu weit vom Lagerfeuer entfernt – du kannst nicht essen."),
                            true
                    );
                    return TypedActionResult.fail(stack);
                } else {
                    return TypedActionResult.pass(stack);
                }
            }

            // Original Logik
            switch (config.getMode()) {
                case VANILLA_SPECIAL_FOODS -> {
                    if (isSpecialFood) return TypedActionResult.pass(stack);
                }
                case NO_FOOD -> {
                    if (stack.get(DataComponentTypes.FOOD) != null)  return TypedActionResult.fail(stack);
                }
                case ALL_FOODS -> {
                    // Immer erlaubt
                }
                default -> {
                }
            }

            return TypedActionResult.pass(stack);
        });

        // ----------------------------
        // Block-Use Event (für Kuchen)
        // ----------------------------
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (config.getMode() != NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE) {
                return ActionResult.PASS;
            }

            if (world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.CAKE) {
                // Prüfen ob Spieler in Campfire-Nähe ist
                boolean nearCampfire = false;
                BlockPos playerPos = player.getBlockPos();

                for (int x = -3; x <= 3; x++) {
                    for (int y = -3; y <= 3; y++) {
                        for (int z = -3; z <= 3; z++) {
                            BlockPos checkPos = playerPos.add(x, y, z);
                            if (world.getBlockState(checkPos).getBlock() == Blocks.CAMPFIRE ||
                                    world.getBlockState(checkPos).getBlock() == Blocks.SOUL_CAMPFIRE) {
                                nearCampfire = true;
                                break;
                            }
                        }
                        if (nearCampfire) break;
                    }
                    if (nearCampfire) break;
                }

                if (!nearCampfire) {
                    player.sendMessage(
                            net.minecraft.text.Text.literal("Kuchen essen geht nur in der Nähe eines Lagerfeuers."),
                            true
                    );
                    return ActionResult.FAIL; // Blockiert Kuchen-Essen
                }
            }

            return ActionResult.PASS;
        });
    }
}

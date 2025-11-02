package de.zaryon.nohunger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import de.zaryon.nohunger.config.NoHungerConfig;

public class NoHungerMod implements ModInitializer {

    private final NoHungerConfig config = NoHungerConfig.getInstance();

    @Override
    public void onInitialize() {

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

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (config.getMode() == NoHungerConfig.HungerMode.NORMAL) {
                return ActionResult.PASS;
            }

            boolean isSpecialFood = stack.get(DataComponentTypes.FOOD) != null && (
                    stack.getItem() == Items.GOLDEN_APPLE ||
                            stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE ||
                            stack.getItem() == Items.SUSPICIOUS_STEW
            );

            if (config.getMode() == NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE && stack.get(DataComponentTypes.FOOD) != null) {
                if (!isNearCampfire(player.getBlockPos(), world)) {
                    player.sendMessage(
                            net.minecraft.text.Text.translatable("message.nohunger.campfire_too_far"),
                            true
                    );
                    return ActionResult.FAIL;
                } else {
                    return ActionResult.PASS;
                }
            }

            switch (config.getMode()) {
                case VANILLA_SPECIAL_FOODS -> {
                    if (isSpecialFood) return ActionResult.PASS;
                }
                case NO_FOOD -> {
                    if (stack.get(DataComponentTypes.FOOD) != null) return ActionResult.FAIL;
                }
                case ALL_FOODS -> {

                }
                default -> {
                }
            }

            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (config.getMode() != NoHungerConfig.HungerMode.SURVIVAL_CAMPFIRE) {
                return ActionResult.PASS;
            }

            if (world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.CAKE) {
                if (!isNearCampfire(player.getBlockPos(), world)) {
                    player.sendMessage(
                            net.minecraft.text.Text.translatable("message.nohunger.cake_distance_error"),
                            true
                    );
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });
    }

    private boolean isNearCampfire(BlockPos playerPos, net.minecraft.world.World world) {
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    BlockState state = world.getBlockState(checkPos);

                    if ((state.getBlock() == Blocks.CAMPFIRE || state.getBlock() == Blocks.SOUL_CAMPFIRE)
                            && state.get(CampfireBlock.LIT)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
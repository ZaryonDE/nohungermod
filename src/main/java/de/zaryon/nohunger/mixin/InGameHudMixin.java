package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class InGameHudMixin {

    @Inject(
            method = "drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockHungerTextures(Identifier texture, int x, int y, int width, int height, CallbackInfo ci) {
        // Prüfe ob Hunger-Effekt aktiv ist
        PlayerEntity player = MinecraftClient.getInstance().player;
        boolean hasHungerEffect = player != null && player.hasStatusEffect(StatusEffects.HUNGER);

        // Wenn Hunger-Effekt aktiv: Hungerleiste IMMER anzeigen
        if (hasHungerEffect) {
            return; // Zeige Hungerleiste
        }

        // Sonst: Config-Einstellung beachten
        if (!NoHungerConfig.getInstance().isShowHungerBar()) {
            String path = texture.getPath();

            if (path.contains("food_") ||
                    path.contains("hunger_") ||
                    path.equals("hud/food_empty") ||
                    path.equals("hud/food_half") ||
                    path.equals("hud/food_full")) {

                ci.cancel();
            }
        }
    }
}
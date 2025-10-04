package de.zaryon.nohunger.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class InGameHudMixin {

    /**
     * Fängt ALLE Texture-Draw-Aufrufe ab und blockiert nur Hunger-Texturen,
     * wenn die Config dies verlangt.
     */
    @Inject(
            method = "drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockHungerTextures(Identifier texture, int x, int y, int width, int height, CallbackInfo ci) {
        if (!NoHungerConfig.getInstance().isShowHungerBar()) {
            String path = texture.getPath();

            // Blockiere nur Hunger-Icons (nicht Luft, Herzen, Rüstung)
            if (path.contains("food_") ||
                    path.contains("hunger_") ||
                    path.equals("hud/food_empty") ||
                    path.equals("hud/food_half") ||
                    path.equals("hud/food_full")) {

                ci.cancel(); // Zeichne diese Textur nicht
            }
        }
    }
}
package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawableHelper.class)
public abstract class InGameHudMixin {

    @Inject(
            method = "drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void blockHungerTextures(MatrixStack matrices, int x, int y, int u, int v, int width, int height, CallbackInfo ci) {
        if (!NoHungerConfig.getInstance().isShowHungerBar()) {
            // Hunger-Icons haben spezifische UV-Koordinaten
            // Normale Hungerleiste: v=27 (leer), v=36 (voll)
            // Grüne Drumsticks: v=45 (voll), v=54 (leer)
            if ((v == 27 || v == 36 || v == 45 || v == 54) && u >= 16 && u <= 79) {
                ci.cancel(); // Blockiere diese Textur
            }
        }
    }
}
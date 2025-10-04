package de.zaryon.nohunger.mixin;

import de.zaryon.nohunger.config.NoHungerConfig;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Unique
    private static final Identifier HUNGER_TEXTURE = new Identifier("minecraft", "textures/gui/icons.png");

    /**
     * Blockiert nur das Rendern der Hungerleiste inkl. grüner Drumsticks vom Hunger-Effekt.
     * Andere HUD-Elemente (Herzen, Rüstung, Luftblasen) bleiben sichtbar.
     */
    @Redirect(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
            )
    )
    private void hideHungerOnly(DrawContext ctx, Identifier texture, int x, int y, int u, int v, int width, int height) {
        boolean hideHunger = !NoHungerConfig.getInstance().isShowHungerBar();

        if (hideHunger && texture.equals(HUNGER_TEXTURE) && isHungerIcon(u, v)) {
            // Hungerleiste überspringen
            return;
        }

        // Alles andere normal zeichnen
        ctx.drawTexture(texture, x, y, u, v, width, height);
    }

    /**
     * Prüft, ob das Symbol zur Hungerleiste gehört.
     * Deckt normale und grüne (verrottetes Fleisch) Hunger-Symbole ab.
     */
    @Unique
    private boolean isHungerIcon(int u, int v) {
        // Normale Hungerleiste: leer (v=27), voll (v=36)
        if ((v == 27 || v == 36) && u >= 16 && u <= 71) return true;

        // Grüne Drumsticks / verrottetes Fleisch: leer (v=54), voll (v=45)
        if ((v == 45 || v == 54) && u >= 16 && u <= 71) return true;

        return false;
    }
}

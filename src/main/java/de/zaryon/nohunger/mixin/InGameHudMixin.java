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
    private static final Identifier FOOD_EMPTY_SPRITE = new Identifier("minecraft", "hud/food_empty");
    @Unique
    private static final Identifier FOOD_FULL_SPRITE = new Identifier("minecraft", "hud/food_full");
    @Unique
    private static final Identifier FOOD_HALF_SPRITE = new Identifier("minecraft", "hud/food_half");


    @Redirect(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",

                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void hideHungerOnly(DrawContext ctx, Identifier texture, int x, int y, int width, int height) {
        boolean hideHunger = !NoHungerConfig.getInstance().isShowHungerBar();

        if (hideHunger) {
            if (texture.equals(FOOD_EMPTY_SPRITE) ||
                    texture.equals(FOOD_FULL_SPRITE) ||
                    texture.equals(FOOD_HALF_SPRITE)) {

                return;
            }
        }
        
        ctx.drawGuiTexture(texture, x, y, width, height);
    }
}
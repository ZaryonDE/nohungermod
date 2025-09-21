package de.zaryon.nohunger.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.zaryon.nohunger.config.NoHungerConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class NoHungerModClient implements ClientModInitializer, ModMenuApi {

    @Override
    public void onInitializeClient() {
        // optional: Client-spezifische Logik
    }

    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return parent -> NoHungerConfig.getInstance().createConfigScreen().setParentScreen(parent).build();
    }

    // Methode zur Bereitstellung der Beschreibung an ModMenu
    public Text getDescription() {
        return Text.translatable("mod.nohunger.description");
    }
}
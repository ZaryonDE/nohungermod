package de.zaryon.nohunger.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.Difficulty;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class NoHungerConfig {

    public enum HungerMode {
        NORMAL("config.nohunger.mode.normal", "config.nohunger.tooltip.normal"),
        VANILLA_SPECIAL_FOODS("config.nohunger.mode.vanilla_special_foods", "config.nohunger.tooltip.vanilla_special_foods"),
        ALL_FOODS("config.nohunger.mode.all_foods", "config.nohunger.tooltip.all_foods"),
        NO_FOOD("config.nohunger.mode.no_food", "config.nohunger.tooltip.no_food"),
        SURVIVAL_CAMPFIRE("config.nohunger.mode.survival_campfire", "config.nohunger.tooltip.survival_campfire"); // NEU

        private final String translationKey;
        private final String tooltipKey;

        HungerMode(String translationKey, String tooltipKey) {
            this.translationKey = translationKey;
            this.tooltipKey = tooltipKey;
        }

        public String getTranslationKey() { return translationKey; }
        public String getTooltipKey() { return tooltipKey; }
    }

    private HungerMode mode = HungerMode.NORMAL;
    private boolean showHungerBar = true;

    private boolean peacefulHunger = false;

    private static final String CONFIG_FILE = "config/nohunger.json";
    private static NoHungerConfig INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private NoHungerConfig() {}

    public static NoHungerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoHungerConfig();
            INSTANCE.loadConfig();
        }
        return INSTANCE;
    }

    public HungerMode getMode() { return mode; }
    public void setMode(HungerMode mode) { this.mode = mode; }

    public boolean isShowHungerBar() { return showHungerBar; }
    public void setShowHungerBar(boolean showHungerBar) { this.showHungerBar = showHungerBar; }

    public boolean isPeacefulHunger() { return peacefulHunger; }
    public void setPeacefulHunger(boolean peacefulHunger) { this.peacefulHunger = peacefulHunger; }

    public void loadConfig() {
        Path path = Path.of(CONFIG_FILE);
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                NoHungerConfig loaded = GSON.fromJson(reader, NoHungerConfig.class);
                if (loaded != null) {
                    this.mode = loaded.mode;
                    this.showHungerBar = loaded.showHungerBar;
                    this.peacefulHunger = loaded.peacefulHunger;
                }
            } catch (IOException ignored) {}
        }
    }

    public void saveConfig() {
        try {
            Files.createDirectories(Path.of("config"));
            try (BufferedWriter writer = Files.newBufferedWriter(Path.of(CONFIG_FILE))) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {}
    }

    public ConfigBuilder createConfigScreen() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Text.translatable("config.nohunger.settings"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.nohunger.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        EnumListEntry<NoHungerConfig.HungerMode> modeEntry = entryBuilder
                .startEnumSelector(Text.translatable("config.nohunger.mode.select"), HungerMode.class, mode)
                .setEnumNameProvider(hm -> Text.translatable(((NoHungerConfig.HungerMode) hm).getTranslationKey()))
                .setTooltipSupplier(hm -> {
                    if (hm == HungerMode.SURVIVAL_CAMPFIRE) {
                        return Optional.of(new Text[]{Text.translatable(hm.getTooltipKey())});
                    } else {
                        return Optional.of(new Text[]{Text.translatable(hm.getTooltipKey())});
                    }
                })
                .setSaveConsumer(this::setMode)
                .build();

        general.addEntry(modeEntry);

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("config.nohunger.show_hunger_bar"), showHungerBar)
                .setTooltip(Text.translatable("config.nohunger.show_hunger_bar.tooltip"))
                .setSaveConsumer(this::setShowHungerBar)
                .build());

        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("config.nohunger.peaceful_hunger"), peacefulHunger)
                .setTooltipSupplier(() -> Optional.of(new Text[]{
                        Text.translatable("config.nohunger.peaceful_hunger.tooltip"),
                        Text.literal(" (Only in Peaceful) ").formatted(Formatting.YELLOW)
                }))
                .setSaveConsumer(value -> {
                    ClientWorld world = MinecraftClient.getInstance().world;
                    if (world != null && world.getDifficulty() == Difficulty.PEACEFUL) {
                        setPeacefulHunger(value);
                    } else {
                        setPeacefulHunger(false);
                    }
                })
                .setDefaultValue(false)
                .build());

        builder.setSavingRunnable(this::saveConfig);
        return builder;
    }
}
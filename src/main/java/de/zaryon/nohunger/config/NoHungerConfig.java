package de.zaryon.nohunger.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.EnumListEntry;
import net.minecraft.text.Text;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class NoHungerConfig {

    // HungerMode enum mit Translation + Tooltip Keys
    public enum HungerMode {
        NORMAL("config.nohunger.mode.normal", "config.nohunger.tooltip.normal"),
        VANILLA_SPECIAL_FOODS("config.nohunger.mode.vanilla_special_foods", "config.nohunger.tooltip.vanilla_special_foods"),
        ALL_FOODS("config.nohunger.mode.all_foods", "config.nohunger.tooltip.all_foods"),
        NO_FOOD("config.nohunger.mode.no_food", "config.nohunger.tooltip.no_food");

        private final String translationKey;
        private final String tooltipKey;

        HungerMode(String translationKey, String tooltipKey) {
            this.translationKey = translationKey;
            this.tooltipKey = tooltipKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public String getTooltipKey() {
            return tooltipKey;
        }
    }

    private HungerMode mode = HungerMode.NORMAL;

    private static final String CONFIG_FILE = "config/nohunger.json";

    private static NoHungerConfig INSTANCE;

    private NoHungerConfig() {
        loadConfig();
    }

    public static NoHungerConfig getInstance() {
        if (INSTANCE == null) INSTANCE = new NoHungerConfig();
        return INSTANCE;
    }

    public HungerMode getMode() {
        return mode;
    }

    public void setMode(HungerMode mode) {
        this.mode = mode;
    }

    // Laden & Speichern
    public void loadConfig() {
        Path path = Path.of(CONFIG_FILE);
        if (Files.exists(path)) {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String json = reader.lines().reduce("", (a, b) -> a + b);
                json = json.replace("{", "").replace("}", "").replace("\"", "");
                for (String part : json.split(",")) {
                    String[] kv = part.split(":");
                    if (kv.length != 2) continue;
                    if (kv[0].trim().equals("mode")) {
                        try {
                            mode = HungerMode.valueOf(kv[1].trim());
                        } catch (Exception ignored) {
                        }
                    }
                }
            } catch (IOException ignored) {
            }
        }
    }

    public void saveConfig() {
        try {
            Files.createDirectories(Path.of("config"));
            try (BufferedWriter writer = Files.newBufferedWriter(Path.of(CONFIG_FILE))) {
                writer.write("{\"mode\":\"" + mode.name() + "\"}");
            }
        } catch (IOException ignored) {
        }
    }

    // Helper-Methode für Enum-Namen als Text
    private Text getModeText(HungerMode hungerMode) {
        return switch (hungerMode) {
            case NORMAL -> Text.translatable("config.nohunger.mode.normal");
            case VANILLA_SPECIAL_FOODS -> Text.translatable("config.nohunger.mode.vanilla_special_foods");
            case ALL_FOODS -> Text.translatable("config.nohunger.mode.all_foods");
            case NO_FOOD -> Text.translatable("config.nohunger.mode.no_food");
            default -> Text.literal(hungerMode.name()); // Fallback
        };
    }

    // ModMenu GUI Builder mit translatable Keys + Tooltips
    public ConfigBuilder createConfigScreen() {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Text.translatable("config.nohunger.settings"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.nohunger.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        EnumListEntry<HungerMode> modeEntry = entryBuilder
                .startEnumSelector(Text.translatable("config.nohunger.mode.select"), HungerMode.class, mode)
                .setEnumNameProvider(hungerMode -> Text.translatable(((NoHungerConfig.HungerMode) hungerMode).getTranslationKey()))
                .setTooltipSupplier(hungerMode -> Optional.of(new Text[]{ Text.translatable(((NoHungerConfig.HungerMode) hungerMode).getTooltipKey()) }))
                .setSaveConsumer(this::setMode)
                .build();

        general.addEntry(modeEntry);

        builder.setSavingRunnable(this::saveConfig);

        return builder;
    }
}
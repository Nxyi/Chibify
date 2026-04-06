package net.chibify.chibify.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;

import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfigScreen {
    public static Screen create(Screen parent) {


        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("chibify.config.title"))
                        .setSavingRunnable(ModConfig::save); // save on close

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("chibify.config.category.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        BooleanListEntry enabled = entryBuilder.startBooleanToggle(Text.translatable("chibify.config.general.enabled"), ModConfig.INSTANCE.enabled)
                .setTooltip(Text.translatable("chibify.config.general.enabled.description"))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.enabled = newValue)
                .build();
        general.addEntry(enabled);

        // Self-Chibi option
        BooleanListEntry shrinkSelfEntry = entryBuilder.startBooleanToggle(Text.translatable("chibify.config.general.self-chibi"), ModConfig.INSTANCE.shrinkSelf)
                .setTooltip(Text.translatable("chibify.config.general.self-chibi.description"))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.shrinkSelf = newValue)
                .build();
        general.addEntry(shrinkSelfEntry);

        // Accurate Eye Position option
        BooleanListEntry accurateEyeEntry = entryBuilder.startBooleanToggle(Text.translatable("chibify.config.general.accurate_eye_pos"), ModConfig.INSTANCE.accurateEyeHeight)
                .setTooltip(Text.translatable("chibify.config.general.accurate_eye_pos.description"))
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> ModConfig.INSTANCE.accurateEyeHeight = newValue)
                .build();
        general.addEntry(accurateEyeEntry);



        return builder.build();
    }
}
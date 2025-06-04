package net.chibify.chibify.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModConfigScreen {
    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("chibify.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("chibify.config.category.general"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("chibify.config.general.self-chibi"))
                                .description(OptionDescription.of(Text.translatable("chibify.config.general.self-chibi.description")))
                                .binding(true, () -> ModConfig.INSTANCE.shrinkSelf, newValue -> ModConfig.INSTANCE.shrinkSelf = newValue)
                                .controller(BooleanControllerBuilder::create)
                                .build())

                        .option(Option.<Boolean>createBuilder()
                                .name(Text.translatable("chibify.config.general.accurate_eye_pos"))
                                .description(OptionDescription.of(Text.translatable("chibify.config.general.accurate_eye_pos.description")))
                                .binding(true, () -> ModConfig.INSTANCE.AccurateEyeHeight, newValue -> ModConfig.INSTANCE.AccurateEyeHeight = newValue)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .save(ModConfig::save)
                .build()
                .generateScreen(parent);
    }
}
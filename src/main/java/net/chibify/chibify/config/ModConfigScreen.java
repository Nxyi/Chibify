package net.chibify.chibify.config;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Locale;
import java.util.function.Consumer;

public class ModConfigScreen extends Screen {
    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 32, 32);

    protected ModConfigScreen(Screen parent) {
        super(Component.translatable("chibify.config.title"));
        this.parent = parent;
    }

    public static Screen create(Screen parent) {
        return new ModConfigScreen(parent);
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);

        ModConfig cfg = ModConfig.INSTANCE;
        LinearLayout columns = this.layout.addToContents(LinearLayout.horizontal().spacing(8));
        LinearLayout leftCol = columns.addChild(LinearLayout.vertical().spacing(4));
        LinearLayout rightCol = columns.addChild(LinearLayout.vertical().spacing(4));

        leftCol.addChild(CycleButton.onOffBuilder(cfg.enabled)
            .withTooltip(v -> Tooltip.create(Component.translatable("chibify.config.general.enabled.description")))
            .create(0, 0, 150, 20, Component.translatable("chibify.config.general.enabled"),
                (btn, val) -> cfg.enabled = val));

        leftCol.addChild(CycleButton.onOffBuilder(cfg.shrinkSelf)
            .withTooltip(v -> Tooltip.create(Component.translatable("chibify.config.general.self-chibi.description")))
            .create(0, 0, 150, 20, Component.translatable("chibify.config.general.self-chibi"),
                (btn, val) -> cfg.shrinkSelf = val));

        leftCol.addChild(CycleButton.onOffBuilder(cfg.accurateEyeHeight)
            .withTooltip(v -> Tooltip.create(Component.translatable("chibify.config.general.accurate_eye_pos.description")))
            .create(0, 0, 150, 20, Component.translatable("chibify.config.general.accurate_eye_pos"),
                (btn, val) -> cfg.accurateEyeHeight = val));

        leftCol.addChild(CycleButton.onOffBuilder(cfg.shrinkPhysicalHitbox)
            .withTooltip(v -> Tooltip.create(Component.translatable("chibify.config.general.shrink_hitbox.description")))
            .create(0, 0, 150, 20, Component.translatable("chibify.config.general.shrink_hitbox"),
                (btn, val) -> cfg.shrinkPhysicalHitbox = val));

        leftCol.addChild(createFloatSlider("chibify.config.scale.player", cfg.playerScale, 0.25F, 2.0F,
            val -> cfg.playerScale = val));
        leftCol.addChild(createFloatSlider("chibify.config.scale.hitbox", cfg.hitboxScale, 0.25F, 1.5F,
            val -> cfg.hitboxScale = val));
        leftCol.addChild(createFloatSlider("chibify.config.inventory.preview_y", cfg.inventoryPreviewOffsetY, -1.0F, 1.0F,
            val -> cfg.inventoryPreviewOffsetY = val, "chibify.config.inventory.preview_y.description"));

        rightCol.addChild(createFloatSlider("chibify.config.scale.head", cfg.headScale, 0.4F, 1.8F,
            val -> cfg.headScale = val));
        rightCol.addChild(createFloatSlider("chibify.config.scale.limb_width", cfg.limbWidthScale, 0.3F, 1.5F,
            val -> cfg.limbWidthScale = val));
        rightCol.addChild(createFloatSlider("chibify.config.scale.limb_height", cfg.limbHeightScale, 0.25F, 1.2F,
            val -> cfg.limbHeightScale = val));
        rightCol.addChild(createFloatSlider("chibify.config.elytra.scale", cfg.elytraScale, 0.2F, 1.5F,
            val -> cfg.elytraScale = val));
        rightCol.addChild(createFloatSlider("chibify.config.elytra.offset_y", cfg.elytraOffsetY, -1.0F, 3.0F,
            val -> cfg.elytraOffsetY = val));
        rightCol.addChild(createFloatSlider("chibify.config.elytra.offset_z", cfg.elytraOffsetZ, -1.0F, 1.0F,
            val -> cfg.elytraOffsetZ = val));
        rightCol.addChild(createFloatSlider("chibify.config.elytra.leg_clearance", cfg.elytraLegClearance, 0.0F, 3.0F,
            val -> cfg.elytraLegClearance = val));

        LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(Button.builder(Component.translatable("chibify.config.reset"), btn -> {
            cfg.resetToDefaults();
            ModConfig.save();
            if (this.minecraft != null) {
                this.minecraft.gui.setScreen(new ModConfigScreen(this.parent));
            }
        }).width(150).build());

        footer.addChild(Button.builder(CommonComponents.GUI_DONE, btn -> this.onClose())
            .width(150).build());

        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public void onClose() {
        ModConfig.save();
        if (this.minecraft != null) {
            this.minecraft.gui.setScreen(this.parent);
        }
    }

    private static AbstractSliderButton createFloatSlider(
        String translationKey, float current, float min, float max, Consumer<Float> setter
    ) {
        return createFloatSlider(translationKey, current, min, max, setter, null);
    }

    private static AbstractSliderButton createFloatSlider(
        String translationKey, float current, float min, float max,
        Consumer<Float> setter, String tooltipTranslationKey
    ) {
        double normalized = Mth.clamp((current - min) / (max - min), 0.0D, 1.0D);
        return new AbstractSliderButton(0, 0, 150, 20, CommonComponents.EMPTY, normalized) {
            {
                this.updateMessage();
                if (tooltipTranslationKey != null) {
                    this.setTooltip(Tooltip.create(Component.translatable(tooltipTranslationKey)));
                }
            }

            private float currentVal() {
                return (float) Mth.lerp(this.value, min, max);
            }

            @Override
            protected void updateMessage() {
                String formatted = String.format(Locale.ROOT, "%.2f", currentVal());
                this.setMessage(Component.translatable(translationKey).append(": " + formatted));
            }

            @Override
            protected void applyValue() {
                setter.accept(Math.round(currentVal() * 100.0F) / 100.0F);
            }
        };
    }
}

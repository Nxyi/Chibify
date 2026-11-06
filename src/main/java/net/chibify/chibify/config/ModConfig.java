package net.chibify.chibify.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.chibify.chibify.Chibify;
import net.chibify.chibify.client.ChibifyClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final int CURRENT_CONFIG_VERSION = 6;

    // Old configs do not contain this field; start at version 1 so load() migrates them.
    public int configVersion = 1;
    public static final Path CONFIG_FILE = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("chibify.json");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig INSTANCE = new ModConfig();

    // Core options preserved from Chibify 1.3.1
    public boolean enabled = true;
    public boolean shrinkSelf = false;
    public boolean accurateEyeHeight = false;

    // Extended 26.3 proportion, hitbox & elytra tuning (defaults match 1.3.1)
    public boolean shrinkPhysicalHitbox = false;
    public float playerScale = 1.0F;
    public float hitboxScale = 0.55F;
    public float headScale = 1.0F;
    public float limbWidthScale = 0.7F;
    public float limbHeightScale = 0.5F;
    public float elytraScale = 0.5F;
    public float elytraOffsetY = 0.0F;
    public float elytraOffsetZ = 0.15F;
    // Shortens the Elytra vertically to reduce overlap with shortened legs (0-3).
    public float elytraLegClearance = 1.0F;
    // Fine-tunes only the local-player preview in Survival/Creative inventory screens.
    // Positive and negative values move the preview along the inventory renderer Y axis.
    public float inventoryPreviewOffsetY = 0.0F;

    public void resetToDefaults() {
        this.enabled = true;
        this.shrinkSelf = false;
        this.accurateEyeHeight = false;
        this.shrinkPhysicalHitbox = false;
        this.playerScale = 1.0F;
        this.hitboxScale = 0.55F;
        this.headScale = 1.0F;
        this.limbWidthScale = 0.7F;
        this.limbHeightScale = 0.5F;
        this.elytraScale = 0.5F;
        this.elytraOffsetY = 0.0F;
        this.elytraOffsetZ = 0.15F;
        this.elytraLegClearance = 1.0F;
        this.inventoryPreviewOffsetY = 0.0F;
        this.configVersion = CURRENT_CONFIG_VERSION;
    }

    public void validate() {
        this.playerScale = clamp(this.playerScale, 0.25F, 2.0F);
        this.hitboxScale = clamp(this.hitboxScale, 0.25F, 1.5F);
        this.headScale = clamp(this.headScale, 0.4F, 1.8F);
        this.limbWidthScale = clamp(this.limbWidthScale, 0.3F, 1.5F);
        this.limbHeightScale = clamp(this.limbHeightScale, 0.25F, 1.2F);
        this.elytraScale = clamp(this.elytraScale, 0.2F, 1.5F);
        this.elytraOffsetY = clamp(this.elytraOffsetY, -1.0F, 3.0F);
        this.elytraOffsetZ = clamp(this.elytraOffsetZ, -1.0F, 1.0F);
        this.elytraLegClearance = clamp(this.elytraLegClearance, 0.0F, 3.0F);
        this.inventoryPreviewOffsetY = clamp(this.inventoryPreviewOffsetY, -1.0F, 1.0F);
    }

    private static float clamp(float value, float min, float max) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    public static void load() {
        if (Files.exists(CONFIG_FILE)) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
                if (loaded != null) {
                    boolean migrated = loaded.configVersion < CURRENT_CONFIG_VERSION;
                    if (loaded.configVersion < 2) {
                        // Version 1.3.1's legacy depth offset (-0.05) pulled
                        // the elytra slightly into the back. Move that default
                        // outward once, preserving explicitly tuned values.
                        if (Math.abs(loaded.elytraOffsetZ - (-0.05F)) < 0.001F) {
                            loaded.elytraOffsetZ = 0.15F;
                        }
                    }
                    if (loaded.configVersion < 3) {
                        // The old vertical offset was a small adjustment around
                        // vanilla Y=0. The new renderer now follows the chibi
                        // torso pivot automatically, so reset only the old
                        // untouched default; preserve custom user adjustments.
                        if (Math.abs(loaded.elytraOffsetY - 1.42F) < 0.001F) {
                            loaded.elytraOffsetY = 0.0F;
                        }
                    }
                    if (loaded.configVersion < 6) {
                        // This setting did not exist in older configs. Start at
                        // neutral automatic alignment and let the user tune it.
                        loaded.inventoryPreviewOffsetY = 0.0F;
                    }
                    if (migrated) {
                        loaded.configVersion = CURRENT_CONFIG_VERSION;
                    }
                    loaded.validate();
                    INSTANCE = loaded;
                    if (migrated) {
                        save();
                    }
                }
            } catch (IOException e) {
                Chibify.LOGGER.error("Failed to load Chibify config; restoring defaults", e);
                INSTANCE = new ModConfig();
            }
        } else {
            save();
        }
    }

    public static void save() {
        INSTANCE.configVersion = CURRENT_CONFIG_VERSION;
        INSTANCE.validate();
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            try (OutputStreamWriter writer = new OutputStreamWriter(
                Files.newOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                GSON.toJson(INSTANCE, writer);
            }
            ChibifyClient.refreshLoadedPlayerDimensions();
        } catch (IOException e) {
            Chibify.LOGGER.error("Failed to save Chibify config", e);
        }
    }
}

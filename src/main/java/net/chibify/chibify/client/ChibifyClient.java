package net.chibify.chibify.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.chibify.chibify.Chibify;
import net.chibify.chibify.config.ModConfig;
import net.chibify.chibify.config.ModConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ChibifyClient implements ClientModInitializer {
    public static final KeyMapping.Category CHIBIFY_KEY_CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(Chibify.MOD_ID, "general")
    );

    private static KeyMapping openConfigKey;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        openConfigKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.chibify.open_config",
            InputConstants.Type.KEYBOARD,
            InputConstants.UNKNOWN.getValue(),
            CHIBIFY_KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.consumeClick()) {
                client.gui.setScreen(ModConfigScreen.create(client.gui.screen()));
            }
        });
    }

    public static boolean shouldChibify(Entity entity) {
        ModConfig cfg = ModConfig.INSTANCE;
        if (!cfg.enabled || !(entity instanceof Player)) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && entity == mc.player && !cfg.shrinkSelf) {
            return false;
        }
        return true;
    }

    public static void refreshLoadedPlayerDimensions() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.level != null) {
            for (Player player : mc.level.players()) {
                player.refreshDimensions();
            }
        }
    }
}

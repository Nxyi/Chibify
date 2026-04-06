package net.chibify.chibify.client;

import net.chibify.chibify.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;

public class ChibifyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModConfig.load();
    }

}

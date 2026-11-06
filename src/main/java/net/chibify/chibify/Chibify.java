package net.chibify.chibify;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chibify implements ModInitializer {
    public static final String MOD_ID = "chibify";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Common initializer kept free of client-only classes for safe loading.
    }
}

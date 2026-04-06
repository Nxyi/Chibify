package net.chibify.chibify.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class ModConfig {
    public static final Path CONFIG_FILE = Path.of("config").resolve("chibify" + ".json");

    public static ModConfig INSTANCE = new ModConfig();

    public boolean enabled = true;
    public boolean shrinkSelf = false;
    public boolean accurateEyeHeight = false;

    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        File file = CONFIG_FILE.toFile();

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load Chibify config", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        File file = CONFIG_FILE.toFile();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
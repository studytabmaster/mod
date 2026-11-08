package net.essentialslike.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.essentialslike.EssentialsLikeMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/** config/essentials_like.json の読込・保存。壊れている場合は .bak に退避してデフォルトで再生成する。 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir()
            .resolve(EssentialsLikeMod.MOD_ID + ".json");

    private ConfigManager() {}

    public static ModConfig load() {
        ModConfig config = null;
        if (Files.exists(PATH)) {
            try {
                config = GSON.fromJson(Files.readString(PATH, StandardCharsets.UTF_8), ModConfig.class);
                if (config == null) {
                    throw new JsonParseException("empty config");
                }
            } catch (IOException | RuntimeException e) {
                backupCorrupted();
                EssentialsLikeMod.LOGGER.warn("Config was corrupted; moved to .bak and regenerated with defaults", e);
                config = null;
            }
        }
        if (config == null) {
            config = new ModConfig();
        }
        clamp(config);
        save(config);
        return config;
    }

    public static void save(ModConfig config) {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(config), StandardCharsets.UTF_8);
        } catch (IOException e) {
            EssentialsLikeMod.LOGGER.error("Failed to save config", e);
        }
    }

    private static void backupCorrupted() {
        try {
            Files.move(PATH, PATH.resolveSibling(PATH.getFileName() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            EssentialsLikeMod.LOGGER.error("Failed to back up corrupted config", e);
        }
    }

    /** 範囲外の値を許容範囲に丸める。 */
    static void clamp(ModConfig c) {
        if (c.global == null) c.global = new ModConfig.Global();
        if (c.player == null) c.player = new ModConfig.Player();
        if (c.player.stoneDisabledBlocks == null) c.player.stoneDisabledBlocks = new ArrayList<>();

        ModConfig.Global g = c.global;
        g.breakerSpeedLimit = clamp(g.breakerSpeedLimit, 0, 20);
        g.maxChainBlocks = clamp(g.maxChainBlocks, 1, 4096);
        g.deathWaypointTimeoutMinutes = clamp(g.deathWaypointTimeoutMinutes, 20, 600);

        ModConfig.Player p = c.player;
        p.oreBreakerRadius = clamp(p.oreBreakerRadius, 1, 8);
        p.breakerSizeW = clamp(p.breakerSizeW, 1, 3);
        p.breakerSizeH = clamp(p.breakerSizeH, 1, 3);
        p.breakerSizeD = clamp(p.breakerSizeD, 1, 3);
        p.foodInfoDetail = clamp(p.foodInfoDetail, 0, 3);

        c.schemaVersion = 2;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}

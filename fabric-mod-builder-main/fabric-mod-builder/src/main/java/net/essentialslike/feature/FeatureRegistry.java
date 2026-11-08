package net.essentialslike.feature;

import net.essentialslike.EssentialsLikeMod;
import net.essentialslike.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

/** 機能の登録と一括初期化。新しい機能は EssentialsLikeMod の登録部に追加する。 */
public final class FeatureRegistry {
    private static final List<Feature> COMMON = new ArrayList<>();
    private static final List<Feature> CLIENT = new ArrayList<>();

    private FeatureRegistry() {}

    public static void registerCommon(Feature feature) {
        COMMON.add(feature);
    }

    public static void registerClient(Feature feature) {
        CLIENT.add(feature);
    }

    public static void initCommon(ModConfig config) {
        for (Feature f : COMMON) {
            if (f.isEnabled(config)) {
                f.init(config);
                EssentialsLikeMod.LOGGER.info("Feature enabled: {}", f.id());
            } else {
                EssentialsLikeMod.LOGGER.info("Feature disabled: {}", f.id());
            }
        }
    }

    public static void initClient(ModConfig config) {
        for (Feature f : CLIENT) {
            if (f.isEnabled(config)) {
                f.init(config);
            }
        }
    }
}

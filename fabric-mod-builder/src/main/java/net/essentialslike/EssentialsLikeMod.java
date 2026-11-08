package net.essentialslike;

import net.essentialslike.config.ConfigManager;
import net.essentialslike.config.ModConfig;
import net.essentialslike.feature.FeatureRegistry;
import net.essentialslike.feature.admin.AdminRemoteFeature;
import net.essentialslike.feature.mining.OreBreakerFeature;
import net.essentialslike.feature.mining.SandBreakerFeature;
import net.essentialslike.feature.mining.StoneBreakerFeature;
import net.essentialslike.feature.tree.TreeBreakerFeature;
import net.essentialslike.util.BreakChainManager;
import net.essentialslike.util.BreakDispatcher;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EssentialsLikeMod implements ModInitializer {
    public static final String MOD_ID = "essentials_like";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModConfig config;

    @Override
    public void onInitialize() {
        config = ConfigManager.load();

        // 共通基盤
        BreakDispatcher.init();
        BreakChainManager.init();

        // 機能登録（管理者リモコンを最初に登録してアイテムとパケットを初期化）
        FeatureRegistry.registerCommon(new AdminRemoteFeature());
        // Phase 2: 採掘・ツリー
        FeatureRegistry.registerCommon(new OreBreakerFeature());
        FeatureRegistry.registerCommon(new StoneBreakerFeature());
        FeatureRegistry.registerCommon(new SandBreakerFeature());
        FeatureRegistry.registerCommon(new TreeBreakerFeature());

        FeatureRegistry.initCommon(config);
        LOGGER.info("EssentialsLike loaded");
    }

    public static ModConfig getConfig() {
        return config;
    }
}

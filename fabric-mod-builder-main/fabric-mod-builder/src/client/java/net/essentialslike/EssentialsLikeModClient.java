package net.essentialslike;

import net.essentialslike.feature.FeatureRegistry;
import net.fabricmc.api.ClientModInitializer;

public class EssentialsLikeModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPacketHandler.init();
        FeatureRegistry.initClient(EssentialsLikeMod.getConfig());
        EssentialsLikeMod.LOGGER.info("EssentialsLike client loaded");
    }
}

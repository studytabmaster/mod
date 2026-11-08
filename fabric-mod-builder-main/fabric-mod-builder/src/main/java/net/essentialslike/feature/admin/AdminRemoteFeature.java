package net.essentialslike.feature.admin;

import net.essentialslike.config.ModConfig;
import net.essentialslike.feature.Feature;
import net.essentialslike.network.ServerPacketHandler;

/** 管理者リモコン機能の初期化エントリ。 */
public final class AdminRemoteFeature implements Feature {
    @Override public String id() { return "admin_remote"; }
    @Override public boolean isEnabled(ModConfig config) { return true; }

    @Override
    public void init(ModConfig config) {
        AdminItems.register();
        ServerPacketHandler.init();
    }
}

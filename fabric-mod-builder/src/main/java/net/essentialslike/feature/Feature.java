package net.essentialslike.feature;

import net.essentialslike.config.ModConfig;

/** 機能の共通インターフェース。 */
public interface Feature {
    /** 機能の識別名（ログ・設定用）。 */
    String id();

    /** 設定上で有効かどうか。 */
    boolean isEnabled(ModConfig config);

    /** 初期化（サーバー/共通）。 */
    void init(ModConfig config);
}

package net.essentialslike.network;

import net.minecraft.util.Identifier;

/**
 * パケット ID の定数。
 * C2S（クライアント→サーバー）：GUI の変更をサーバーへ送る。
 * S2C（サーバー→クライアント）：サーバーの設定をクライアントへ同期する。
 */
public final class Packets {
    public static final Identifier C2S_SET_GLOBAL_BOOL   = new Identifier("essentials_like", "set_global_bool");
    public static final Identifier C2S_SET_GLOBAL_INT    = new Identifier("essentials_like", "set_global_int");
    public static final Identifier C2S_SET_PLAYER_BOOL   = new Identifier("essentials_like", "set_player_bool");
    public static final Identifier C2S_SET_PLAYER_INT    = new Identifier("essentials_like", "set_player_int");
    public static final Identifier S2C_SYNC_CONFIG       = new Identifier("essentials_like", "sync_config");
    private Packets() {}
}

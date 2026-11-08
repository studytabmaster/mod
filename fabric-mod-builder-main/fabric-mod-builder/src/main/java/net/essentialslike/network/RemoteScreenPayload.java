package net.essentialslike.network;

import net.minecraft.util.Identifier;

/** サーバー → クライアントへ GUI を開かせるためのパケット ID。 */
public final class RemoteScreenPayload {
    public static final Identifier OPEN_REMOTE        = new Identifier("essentials_like", "open_remote");
    public static final Identifier OPEN_PLAYER_REMOTE = new Identifier("essentials_like", "open_player_remote");
    private RemoteScreenPayload() {}
}

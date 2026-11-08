package net.essentialslike;

import com.google.gson.Gson;
import net.essentialslike.config.ModConfig;
import net.essentialslike.network.Packets;
import net.essentialslike.network.RemoteScreenPayload;
import net.essentialslike.screen.AdminRemoteScreen;
import net.essentialslike.screen.PlayerRemoteScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

/** クライアント側パケット受信：設定の同期と GUI 開閉。 */
public final class ClientPacketHandler {
    private static final Gson GSON = new Gson();
    /** 最後にサーバーから受け取った設定（クライアント側で GUI 表示に使う）。 */
    public static volatile ModConfig serverConfig = new ModConfig();

    private ClientPacketHandler() {}

    public static void init() {
        // 設定同期
        ClientPlayNetworking.registerGlobalReceiver(Packets.S2C_SYNC_CONFIG, (client, handler, buf, responseSender) -> {
            String json = buf.readString();
            client.execute(() -> {
                serverConfig = GSON.fromJson(json, ModConfig.class);
                // 開いている GUI があれば更新する
                if (client.currentScreen instanceof AdminRemoteScreen s) s.refreshFromConfig(serverConfig);
                if (client.currentScreen instanceof PlayerRemoteScreen s) s.refreshFromConfig(serverConfig);
            });
        });

        // 管理者リモコン画面を開く
        ClientPlayNetworking.registerGlobalReceiver(RemoteScreenPayload.OPEN_REMOTE, (client, handler, buf, responseSender) ->
                client.execute(() -> MinecraftClient.getInstance().setScreen(new AdminRemoteScreen(serverConfig))));

        // 個別設定画面を開く
        ClientPlayNetworking.registerGlobalReceiver(RemoteScreenPayload.OPEN_PLAYER_REMOTE, (client, handler, buf, responseSender) ->
                client.execute(() -> MinecraftClient.getInstance().setScreen(new PlayerRemoteScreen(serverConfig))));
    }
}

package net.essentialslike.network;

import com.google.gson.Gson;
import net.essentialslike.EssentialsLikeMod;
import net.essentialslike.config.ConfigManager;
import net.essentialslike.config.ModConfig;
import net.essentialslike.util.PermissionUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class ServerPacketHandler {
    private static final Gson GSON = new Gson();
    private ServerPacketHandler() {}

    public static void init() {
        // グローバル bool 設定（管理者のみ）
        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_SET_GLOBAL_BOOL, (server, player, handler, buf, responseSender) -> {
            String field = buf.readString(64);
            boolean value = buf.readBoolean();
            server.execute(() -> setGlobalBool(server, player, field, value));
        });

        // グローバル int 設定（管理者のみ）
        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_SET_GLOBAL_INT, (server, player, handler, buf, responseSender) -> {
            String field = buf.readString(64);
            int value = buf.readInt();
            server.execute(() -> setGlobalInt(server, player, field, value));
        });

        // プレイヤー個別 bool 設定
        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_SET_PLAYER_BOOL, (server, player, handler, buf, responseSender) -> {
            String field = buf.readString(64);
            boolean value = buf.readBoolean();
            server.execute(() -> setPlayerBool(server, player, field, value));
        });

        // プレイヤー個別 int 設定
        ServerPlayNetworking.registerGlobalReceiver(Packets.C2S_SET_PLAYER_INT, (server, player, handler, buf, responseSender) -> {
            String field = buf.readString(64);
            int value = buf.readInt();
            server.execute(() -> setPlayerInt(server, player, field, value));
        });

        // 接続時に設定を同期する
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                syncConfigTo(server, handler.player));
    }

    // ---- グローバル設定（管理者のみ）----
    private static void setGlobalBool(MinecraftServer server, ServerPlayerEntity player, String field, boolean value) {
        if (!PermissionUtil.isAdmin(player)) {
            player.sendMessage(Text.translatable("essentials_like.common.admin_required"), true);
            return;
        }
        ModConfig cfg = EssentialsLikeMod.getConfig();
        try {
            ModConfig.Global.class.getField(field).setBoolean(cfg.global, value);
            ConfigManager.save(cfg);
            broadcastConfig(server);
            EssentialsLikeMod.LOGGER.info("[Admin] {} set global.{} = {}", player.getName().getString(), field, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            EssentialsLikeMod.LOGGER.warn("setGlobalBool: unknown field '{}'", field);
        }
    }

    private static void setGlobalInt(MinecraftServer server, ServerPlayerEntity player, String field, int value) {
        if (!PermissionUtil.isAdmin(player)) {
            player.sendMessage(Text.translatable("essentials_like.common.admin_required"), true);
            return;
        }
        ModConfig cfg = EssentialsLikeMod.getConfig();
        try {
            ModConfig.Global.class.getField(field).setInt(cfg.global, value);
            ConfigManager.clamp(cfg);
            ConfigManager.save(cfg);
            broadcastConfig(server);
            EssentialsLikeMod.LOGGER.info("[Admin] {} set global.{} = {}", player.getName().getString(), field, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            EssentialsLikeMod.LOGGER.warn("setGlobalInt: unknown field '{}'", field);
        }
    }

    // ---- 個別設定 ----
    private static void setPlayerBool(MinecraftServer server, ServerPlayerEntity player, String field, boolean value) {
        ModConfig cfg = EssentialsLikeMod.getConfig();
        try {
            ModConfig.Player.class.getField(field).setBoolean(cfg.player, value);
            ConfigManager.save(cfg);
            syncConfigTo(server, player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            EssentialsLikeMod.LOGGER.warn("setPlayerBool: unknown field '{}'", field);
        }
    }

    private static void setPlayerInt(MinecraftServer server, ServerPlayerEntity player, String field, int value) {
        ModConfig cfg = EssentialsLikeMod.getConfig();
        try {
            ModConfig.Player.class.getField(field).setInt(cfg.player, value);
            ConfigManager.clamp(cfg);
            ConfigManager.save(cfg);
            syncConfigTo(server, player);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            EssentialsLikeMod.LOGGER.warn("setPlayerInt: unknown field '{}'", field);
        }
    }

    // ---- 同期 ----
    public static void syncConfigTo(MinecraftServer server, ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(GSON.toJson(EssentialsLikeMod.getConfig()));
        ServerPlayNetworking.send(player, Packets.S2C_SYNC_CONFIG, buf);
    }

    private static void broadcastConfig(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            syncConfigTo(server, player);
        }
    }
}

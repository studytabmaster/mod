package net.essentialslike.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

/** 権限判定。管理者（OP）のみ管理者設定の変更・危険機能の操作を許可する。 */
public final class PermissionUtil {
    /** OP レベル 2 以上を管理者とみなす（/op 相当）。 */
    public static final int ADMIN_PERMISSION_LEVEL = 2;

    private PermissionUtil() {}

    public static boolean isAdmin(ServerPlayerEntity player) {
        return player != null && player.hasPermissionLevel(ADMIN_PERMISSION_LEVEL);
    }

    public static boolean isAdmin(ServerCommandSource source) {
        return source.hasPermissionLevel(ADMIN_PERMISSION_LEVEL);
    }

    /** 管理者リモコンを使用できるか（OP のみ、クリエイティブ限定入手とは別に判定）。 */
    public static boolean canUseAdminRemote(ServerPlayerEntity player) {
        return isAdmin(player);
    }
}

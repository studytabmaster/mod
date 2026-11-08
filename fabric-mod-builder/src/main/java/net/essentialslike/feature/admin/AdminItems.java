package net.essentialslike.feature.admin;

import net.essentialslike.EssentialsLikeMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** リモコン系アイテムの登録。 */
public final class AdminItems {
    public static final AdminRemoteItem  ADMIN_REMOTE  = new AdminRemoteItem();
    public static final PlayerRemoteItem PLAYER_REMOTE = new PlayerRemoteItem();

    private AdminItems() {}

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(EssentialsLikeMod.MOD_ID, "admin_remote"),  ADMIN_REMOTE);
        Registry.register(Registries.ITEM, new Identifier(EssentialsLikeMod.MOD_ID, "player_remote"), PLAYER_REMOTE);
    }
}

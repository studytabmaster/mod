package net.essentialslike.feature.admin;

import net.essentialslike.network.RemoteScreenPayload;
import net.essentialslike.util.PermissionUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * 管理者リモコンアイテム。
 * 右クリックでリモコン GUI を開く（クライアント側に画面オープン信号を送る）。
 * 管理者以外は「管理者権限が必要です」と表示して何もしない。
 */
public final class AdminRemoteItem extends Item {
    public AdminRemoteItem() {
        super(new Settings().maxCount(1).fireproof());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.success(user.getStackInHand(hand));
        ServerPlayerEntity player = (ServerPlayerEntity) user;
        if (!PermissionUtil.isAdmin(player)) {
            player.sendMessage(Text.translatable("essentials_like.common.admin_required"), true);
            return TypedActionResult.fail(user.getStackInHand(hand));
        }
        // クライアントへ「リモコン画面を開け」の信号を送る
        ServerPlayNetworking.send(player, RemoteScreenPayload.OPEN_REMOTE, PacketByteBufs.empty());
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public boolean hasGlint(ItemStack stack) { return true; }
}

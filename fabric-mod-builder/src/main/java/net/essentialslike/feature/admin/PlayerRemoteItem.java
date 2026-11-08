package net.essentialslike.feature.admin;

import net.essentialslike.network.RemoteScreenPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * プレイヤー個別設定リモコン。誰でも使える。
 * 右クリックで個別設定 GUI を開く。
 */
public final class PlayerRemoteItem extends Item {
    public PlayerRemoteItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.success(user.getStackInHand(hand));
        ServerPlayNetworking.send((ServerPlayerEntity) user, RemoteScreenPayload.OPEN_PLAYER_REMOTE, PacketByteBufs.empty());
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}

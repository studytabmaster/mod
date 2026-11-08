package net.essentialslike.feature.mining;

import net.essentialslike.EssentialsLikeMod;
import net.essentialslike.config.ModConfig;
import net.essentialslike.feature.Feature;
import net.essentialslike.util.BreakChainManager;
import net.essentialslike.util.BreakDispatcher;
import net.essentialslike.util.BreakHelper;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** ストーンブレーカー：ツルハシで石系を壊すと、向いている方向の範囲（最大3×3×3）をまとめて破壊する。 */
public final class StoneBreakerFeature implements Feature {
    @Override
    public String id() {
        return "stone_breaker";
    }

    @Override
    public boolean isEnabled(ModConfig config) {
        return config.global.stoneBreaker;
    }

    @Override
    public void init(ModConfig config) {
        BreakDispatcher.register(BreakDispatcher.PRI_STONE, StoneBreakerFeature::handle);
    }

    private static boolean handle(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        ModConfig cfg = EssentialsLikeMod.getConfig();
        if (!cfg.global.stoneBreaker) return false;
        if (!(tool.getItem() instanceof PickaxeItem)) return false;
        if (!MiningBlocks.isStoneTarget(state, cfg, tool) || MiningBlocks.isDisabledByPlayer(state, cfg)) return false;
        if (!BreakHelper.sneakOk(player, cfg.player.stoneInvertSneak)) return false;

        BreakChainManager.submit(BreakHelper.areaJob(world, player, tool, pos,
                s -> MiningBlocks.isStoneTarget(s, cfg, tool) && !MiningBlocks.isDisabledByPlayer(s, cfg),
                cfg.player, cfg.global.maxChainBlocks));
        return true;
    }
}

package net.essentialslike.feature.mining;

import net.essentialslike.EssentialsLikeMod;
import net.essentialslike.config.ModConfig;
import net.essentialslike.feature.Feature;
import net.essentialslike.util.BreakDispatcher;
import net.essentialslike.util.BreakChainManager;
import net.essentialslike.util.BreakHelper;
import net.essentialslike.util.BreakJob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** 鉱石ブレーカー：ツルハシで鉱石を壊すと、同種の鉱石を BFS で連鎖破壊する。 */
public final class OreBreakerFeature implements Feature {
    @Override
    public String id() {
        return "ore_breaker";
    }

    @Override
    public boolean isEnabled(ModConfig config) {
        return config.global.oreBreaker;
    }

    @Override
    public void init(ModConfig config) {
        BreakDispatcher.register(BreakDispatcher.PRI_ORE, OreBreakerFeature::handle);
    }

    private static boolean handle(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        ModConfig cfg = EssentialsLikeMod.getConfig();
        if (!cfg.global.oreBreaker || !MiningBlocks.isOre(state)) return false;
        if (!(tool.getItem() instanceof PickaxeItem)) return false;
        if (!BreakHelper.sneakOk(player, cfg.player.oreInvertSneak)) return false;

        Block origin = state.getBlock();
        int radius = cfg.player.oreBreakerRadius;
        BreakJob job = new BreakJob(world, player, tool, true, true,
                s -> s.isOf(origin),
                p -> BreakHelper.withinRadius(pos, p, radius),
                pos, cfg.global.maxChainBlocks, null);
        job.recordBreak(pos);
        job.expandFrom(pos);
        BreakChainManager.submit(job);
        return true;
    }
}

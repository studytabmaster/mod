package net.essentialslike.feature.mining;

import net.essentialslike.EssentialsLikeMod;
import net.essentialslike.config.ModConfig;
import net.essentialslike.feature.Feature;
import net.essentialslike.util.BreakChainManager;
import net.essentialslike.util.BreakDispatcher;
import net.essentialslike.util.BreakHelper;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

/** サンドブレーカー：シャベルで砂系・土系を壊すと、ストーンブレーカーと同じ範囲を破壊する。 */
public final class SandBreakerFeature implements Feature {
    @Override
    public String id() {
        return "sand_breaker";
    }

    @Override
    public boolean isEnabled(ModConfig config) {
        return config.global.sandBreaker;
    }

    @Override
    public void init(ModConfig config) {
        BreakDispatcher.register(BreakDispatcher.PRI_SAND, SandBreakerFeature::handle);
    }

    private static boolean handle(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        ModConfig cfg = EssentialsLikeMod.getConfig();
        if (!cfg.global.sandBreaker) return false;
        if (!(tool.getItem() instanceof ShovelItem)) return false;
        if (!MiningBlocks.isSandTarget(state, cfg)) return false;
        if (!BreakHelper.sneakOk(player, cfg.player.sandInvertSneak)) return false;

        BreakChainManager.submit(BreakHelper.areaJob(world, player, tool, pos,
                s -> MiningBlocks.isSandTarget(s, cfg),
                cfg.player, cfg.global.maxChainBlocks));
        return true;
    }
}

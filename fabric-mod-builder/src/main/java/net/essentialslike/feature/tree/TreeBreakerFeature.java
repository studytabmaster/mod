package net.essentialslike.feature.tree;

import net.essentialslike.EssentialsLikeMod;
import net.essentialslike.config.ModConfig;
import net.essentialslike.feature.Feature;
import net.essentialslike.util.BreakChainManager;
import net.essentialslike.util.BreakDispatcher;
import net.essentialslike.util.BreakHelper;
import net.essentialslike.util.BreakJob;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * ツリーブレーカー：斧で原木を壊すと、接続する同種原木を BFS で伐採する。
 * 葉（リーブスブレーカー）と苗木の再植え付けは伐採完了後に行う。
 */
public final class TreeBreakerFeature implements Feature {
    /** 家判定：立方体範囲内の木材ブロック数がこの値以上なら警告する（伐採は続行）。 */
    private static final int HOUSE_PLANK_THRESHOLD = 8;
    private static final int HOUSE_CHECK_RADIUS = 3;
    private static final int LIMIT_RADIUS = 16;
    private static final int LEAVES_RADIUS = 6;

    @Override
    public String id() {
        return "tree_breaker";
    }

    @Override
    public boolean isEnabled(ModConfig config) {
        return config.global.treeBreaker;
    }

    @Override
    public void init(ModConfig config) {
        BreakDispatcher.register(BreakDispatcher.PRI_TREE, TreeBreakerFeature::handle);
    }

    private static boolean handle(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack tool) {
        ModConfig cfg = EssentialsLikeMod.getConfig();
        if (!cfg.global.treeBreaker || !TreeBlocks.isTreeBlock(state)) return false;
        if (!(tool.getItem() instanceof AxeItem)) return false;
        if (!BreakHelper.sneakOk(player, cfg.player.treeInvertSneak)) return false;

        if (BreakHelper.countNearby(world, player.getBlockPos(), HOUSE_CHECK_RADIUS,
                s -> s.isIn(BlockTags.PLANKS)) >= HOUSE_PLANK_THRESHOLD) {
            player.sendMessage(Text.translatable("essentials_like.tree.house_warning"), true);
        }

        boolean fixed = cfg.global.treeBreakerFixedDurability;
        // 固定モードでは起点の伐採につき耐久を1だけ消費する
        if (fixed && !BreakHelper.damageTool(player, tool, 1)) return true;

        Block origin = state.getBlock();
        boolean limitless = cfg.global.treeBreakerLimitless;
        BreakJob job = new BreakJob(world, player, tool, !fixed, true,
                s -> s.isOf(origin),
                p -> limitless || BreakHelper.withinRadius(pos, p, LIMIT_RADIUS),
                pos, cfg.global.maxChainBlocks,
                j -> afterChop(j, origin));
        job.recordBreak(pos);
        job.expandFrom(pos);
        BreakChainManager.submit(job);
        return true;
    }

    /** 伐採完了後：リーブス破壊と苗木の再植え付け。 */
    private static void afterChop(BreakJob job, Block origin) {
        ModConfig cfg = EssentialsLikeMod.getConfig();
        if (cfg.global.leavesBreaker) {
            scheduleLeaves(job, cfg);
        }
        if (cfg.global.treeReplanting && job.lowest != null) {
            replant(job, origin);
        }
    }

    private static void scheduleLeaves(BreakJob chop, ModConfig cfg) {
        // 葉はシルクタッチ相当（シアーのシルクタッチ）で処理し、プレイヤーの道具の耐久は使わない
        ItemStack shears = new ItemStack(Items.SHEARS);
        shears.addEnchantment(Enchantments.SILK_TOUCH, 1);

        BlockPos center = chop.dropAt;
        BreakJob leaves = new BreakJob(chop.world, chop.player, shears, false, false,
                s -> s.isIn(BlockTags.LEAVES),
                p -> true,
                center, cfg.global.maxChainBlocks, null);
        for (BlockPos p : BlockPos.iterate(center.add(-LEAVES_RADIUS, -LEAVES_RADIUS, -LEAVES_RADIUS),
                center.add(LEAVES_RADIUS, LEAVES_RADIUS, LEAVES_RADIUS))) {
            leaves.enqueue(p.toImmutable());
        }
        BreakChainManager.submit(leaves);
    }

    private static void replant(BreakJob chop, Block origin) {
        Item sapling = TreeBlocks.saplingFor(origin);
        if (sapling == null) return;

        BlockPos base = chop.lowest;
        if (!chop.world.getBlockState(base).isAir()) return;
        BlockState ground = chop.world.getBlockState(base.down());
        if (!(ground.isIn(BlockTags.DIRT) || ground.isOf(Blocks.MUD))) return;

        PlayerInventory inventory = chop.player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(sapling)) {
                chop.world.setBlockState(base, Block.getBlockFromItem(sapling).getDefaultState(), Block.NOTIFY_ALL);
                stack.decrement(1);
                return;
            }
        }
    }
}

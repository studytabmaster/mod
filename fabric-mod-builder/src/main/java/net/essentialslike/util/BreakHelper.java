package net.essentialslike.util;

import net.essentialslike.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** 連鎖・範囲破壊の共通処理。 */
public final class BreakHelper {
    private BreakHelper() {}

    /**
     * シフト判定。デフォルトは「シフト中は発動しない」。invert が true の場合は「シフト中のみ発動」。
     */
    public static boolean sneakOk(ServerPlayerEntity player, boolean invert) {
        boolean sneaking = player.isSneaking();
        return invert ? sneaking : !sneaking;
    }

    /** 道具の耐久を amount 減らす。壊れた場合は false。クリエイティブ・耐久なしは常に true。 */
    public static boolean damageTool(ServerPlayerEntity player, ItemStack tool, int amount) {
        if (tool.isEmpty()) return false;
        if (player.isCreative() || !tool.isDamageable()) return true;
        tool.damage(amount, player, p -> {});
        return !tool.isEmpty();
    }

    /** ブロックを消し、ドロップを dropAt 付近に集約してスポーンさせる。道具のエンチャントを適用する。 */
    public static void breakAndDrop(ServerWorld world, ServerPlayerEntity player, ItemStack tool,
                                    BlockPos pos, BlockState state, BlockPos dropAt) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        List<ItemStack> drops = Block.getDroppedStacks(state, world, pos, blockEntity, player, tool);
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
        for (ItemStack stack : drops) {
            spawnDrop(world, dropAt, stack);
        }
    }

    public static void spawnDrop(ServerWorld world, BlockPos at, ItemStack stack) {
        ItemEntity entity = new ItemEntity(world, at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, stack);
        entity.setToDefaultPickupDelay();
        world.spawnEntity(entity);
    }

    /** 直方体の範囲判定（Chebyshev 距離）。 */
    public static boolean withinRadius(BlockPos origin, BlockPos pos, int radius) {
        return Math.abs(pos.getX() - origin.getX()) <= radius
                && Math.abs(pos.getY() - origin.getY()) <= radius
                && Math.abs(pos.getZ() - origin.getZ()) <= radius;
    }

    /** プレイヤーの向きから奥行き方向を決める。上下を向いている場合は上下方向。 */
    public static Direction depthDirection(ServerPlayerEntity player) {
        float pitch = player.getPitch();
        if (pitch <= -60f) return Direction.UP;
        if (pitch >= 60f) return Direction.DOWN;
        return player.getHorizontalFacing();
    }

    /**
     * 破壊面を基準にした範囲（原点は含まない）。
     * 奥行きは depth 方向へ w/h/d で広げ、幅・高さは中心寄せ。
     */
    public static List<BlockPos> area(BlockPos origin, Direction depth, int w, int h, int d) {
        Direction right;
        Direction up;
        if (depth.getAxis().isVertical()) {
            right = Direction.EAST;
            up = Direction.NORTH;
        } else {
            right = depth.rotateYClockwise();
            up = Direction.UP;
        }
        List<BlockPos> out = new ArrayList<>();
        for (int i = 0; i < d; i++) {
            for (int a = -(w - 1) / 2; a <= w / 2; a++) {
                for (int b = -(h - 1) / 2; b <= h / 2; b++) {
                    if (i == 0 && a == 0 && b == 0) continue;
                    out.add(origin.offset(depth, i).offset(right, a).offset(up, b));
                }
            }
        }
        return out;
    }

    /** 範囲破壊ジョブを作る（ストーン・サンド共通）。 */
    public static BreakJob areaJob(ServerWorld world, ServerPlayerEntity player, ItemStack tool, BlockPos origin,
                                   Predicate<BlockState> accept, ModConfig.Player p, int maxBlocks) {
        BreakJob job = new BreakJob(world, player, tool, true, false, accept, pos -> true,
                origin, maxBlocks, null);
        for (BlockPos pos : area(origin, depthDirection(player), p.breakerSizeW, p.breakerSizeH, p.breakerSizeD)) {
            job.enqueue(pos);
        }
        return job;
    }

    /** center を中心とした立方体内で、条件に合うブロック数を数える。 */
    public static int countNearby(ServerWorld world, BlockPos center, int radius, Predicate<BlockState> test) {
        int count = 0;
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, -radius, -radius), center.add(radius, radius, radius))) {
            if (test.test(world.getBlockState(pos))) count++;
        }
        return count;
    }
}

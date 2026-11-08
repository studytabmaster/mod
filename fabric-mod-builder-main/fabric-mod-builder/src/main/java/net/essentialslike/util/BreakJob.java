package net.essentialslike.util;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** 1回の連鎖破壊の状態。再帰を使わず、キュー（BFS）で探索する。 */
public final class BreakJob {
    /** 26近傍のオフセット。上方向を先に並べ、木の伐採で上を優先する。 */
    private static final int[][] NEIGHBORS = buildNeighbors();

    public final ServerWorld world;
    public final ServerPlayerEntity player;
    public final ItemStack tool;
    /** true なら1ブロックごとに道具の耐久を1消費する */
    public final boolean consumesToolPerBlock;
    /** true なら破壊したブロックの近傍を探索対象に加える（BFS） */
    public final boolean expandNeighbors;
    public final Predicate<BlockState> accept;
    public final Predicate<BlockPos> inBounds;
    public final BlockPos dropAt;
    public final int maxBlocks;
    private final Consumer<BreakJob> onFinish;

    private final ArrayDeque<BlockPos> queue = new ArrayDeque<>();
    private final Set<BlockPos> visited = new HashSet<>();
    public int broken;
    /** 破壊した中で最も低い位置（苗木の植え付けに使用） */
    public BlockPos lowest;
    public boolean done;
    private boolean finished;

    public BreakJob(ServerWorld world, ServerPlayerEntity player, ItemStack tool,
                    boolean consumesToolPerBlock, boolean expandNeighbors,
                    Predicate<BlockState> accept, Predicate<BlockPos> inBounds,
                    BlockPos dropAt, int maxBlocks, Consumer<BreakJob> onFinish) {
        this.world = world;
        this.player = player;
        this.tool = tool;
        this.consumesToolPerBlock = consumesToolPerBlock;
        this.expandNeighbors = expandNeighbors;
        this.accept = accept;
        this.inBounds = inBounds;
        this.dropAt = dropAt;
        this.maxBlocks = maxBlocks;
        this.onFinish = onFinish;
        this.visited.add(dropAt);
    }

    public void enqueue(BlockPos pos) {
        if (visited.add(pos) && inBounds.test(pos)) {
            queue.add(pos);
        }
    }

    public void expandFrom(BlockPos pos) {
        if (!expandNeighbors) return;
        for (int[] o : NEIGHBORS) {
            enqueue(pos.add(o[0], o[1], o[2]));
        }
    }

    public BlockPos poll() {
        return queue.poll();
    }

    public void recordBreak(BlockPos pos) {
        broken++;
        if (lowest == null || pos.getY() < lowest.getY()) {
            lowest = pos;
        }
    }

    public void finish() {
        if (finished) return;
        finished = true;
        done = true;
        if (onFinish != null) {
            onFinish.accept(this);
        }
    }

    private static int[][] buildNeighbors() {
        List<int[]> list = new ArrayList<>();
        for (int dy = 1; dy >= -1; dy--) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    list.add(new int[]{dx, dy, dz});
                }
            }
        }
        return list.toArray(new int[0][]);
    }
}

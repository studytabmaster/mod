package net.essentialslike.util;

import net.essentialslike.EssentialsLikeMod;
import net.essentialslike.config.ModConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * 連鎖破壊ジョブを tick ごとに分散実行する。
 * speedLimit 1〜20 は1tickあたりの破壊数、0 は MSPT による自動調整（開始32、上限256）。
 */
public final class BreakChainManager {
    private static final int AUTO_START = 32;
    private static final int AUTO_MIN = 1;
    private static final int AUTO_MAX = 256;
    private static final float MSPT_HIGH = 45f;
    private static final float MSPT_LOW = 10f;

    private static final List<BreakJob> JOBS = new ArrayList<>();
    private static int autoBudget = AUTO_START;

    private BreakChainManager() {}

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(BreakChainManager::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                JOBS.removeIf(job -> job.player == handler.player));
    }

    public static void submit(BreakJob job) {
        JOBS.add(job);
    }

    private static void tick(MinecraftServer server) {
        if (JOBS.isEmpty()) return;

        adjustAutoBudget(server.getAverageTickTime());
        int speed = EssentialsLikeMod.getConfig().global.breakerSpeedLimit;
        int budget = speed > 0 ? speed : autoBudget;

        // 処理中に追加されたジョブが ConcurrentModification を起こさないよう、スナップショットで回す
        List<BreakJob> current = new ArrayList<>(JOBS);
        JOBS.clear();
        List<BreakJob> keep = new ArrayList<>();

        for (BreakJob job : current) {
            if (budget > 0) {
                budget -= step(job, budget);
            }
            if (job.done) {
                job.finish();
            } else {
                keep.add(job);
            }
        }
        JOBS.addAll(0, keep);
    }

    private static void adjustAutoBudget(float mspt) {
        if (mspt > MSPT_HIGH) {
            autoBudget = Math.max(AUTO_MIN, autoBudget / 2);
        } else if (mspt < MSPT_LOW) {
            autoBudget = Math.min(AUTO_MAX, autoBudget + 8);
        }
    }

    /** ジョブを最大 max 回まで進める。実際に処理した回数を返す。 */
    private static int step(BreakJob job, int max) {
        int used = 0;
        while (used < max) {
            if (job.broken >= job.maxBlocks || !isValid(job)) {
                job.done = true;
                break;
            }
            BlockPos pos = job.poll();
            if (pos == null) {
                job.done = true;
                break;
            }
            BlockState state = job.world.getBlockState(pos);
            if (state.isAir() || !job.accept.test(state)) {
                continue;
            }
            if (job.consumesToolPerBlock && !BreakHelper.damageTool(job.player, job.tool, 1)) {
                job.done = true;
                break;
            }
            BreakGuard.run(() -> BreakHelper.breakAndDrop(job.world, job.player, job.tool, pos, state, job.dropAt));
            job.recordBreak(pos);
            job.expandFrom(pos);
            used++;
        }
        return used;
    }

    private static boolean isValid(BreakJob job) {
        return job.player.networkHandler != null
                && !job.player.isRemoved()
                && job.player.getWorld() == job.world;
    }
}

package net.essentialslike.util;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ブロック破壊後の振り分け。優先順位の小さい順に評価し、最初に発動したハンドラーで打ち切る。
 * 1: 壊せるスポナー / 2: ツリー / 3: 作物 / 4: 鉱石 / 5: 石 / 6: 砂
 */
public final class BreakDispatcher {
    public static final int PRI_SPAWNER = 1;
    public static final int PRI_TREE = 2;
    public static final int PRI_CROP = 3;
    public static final int PRI_ORE = 4;
    public static final int PRI_STONE = 5;
    public static final int PRI_SAND = 6;

    @FunctionalInterface
    public interface Handler {
        /** 発動した場合 true を返す。 */
        boolean handle(ServerWorld world, ServerPlayerEntity player, BlockPos pos, BlockState state, ItemStack tool);
    }

    private record Entry(int priority, Handler handler) {}

    private static final List<Entry> HANDLERS = new ArrayList<>();

    private BreakDispatcher() {}

    public static void init() {
        PlayerBlockBreakEvents.AFTER.register(BreakDispatcher::onAfterBreak);
    }

    public static synchronized void register(int priority, Handler handler) {
        HANDLERS.add(new Entry(priority, handler));
        HANDLERS.sort(Comparator.comparingInt(Entry::priority));
    }

    private static void onAfterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(world instanceof ServerWorld serverWorld) || !(player instanceof ServerPlayerEntity serverPlayer)) return;
        if (BreakGuard.isActive()) return;

        ItemStack tool = serverPlayer.getMainHandStack();
        for (Entry entry : HANDLERS) {
            if (entry.handler().handle(serverWorld, serverPlayer, pos, state, tool)) {
                return;
            }
        }
    }
}

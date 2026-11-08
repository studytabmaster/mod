package net.essentialslike.util;

/** 連鎖破壊中の再帰的な破壊イベント発火を防ぐためのガード。全ブレーカー共通で使用する。 */
public final class BreakGuard {
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private BreakGuard() {}

    public static boolean isActive() {
        return ACTIVE.get();
    }

    public static void run(Runnable action) {
        boolean previous = ACTIVE.get();
        ACTIVE.set(Boolean.TRUE);
        try {
            action.run();
        } finally {
            ACTIVE.set(previous);
        }
    }
}

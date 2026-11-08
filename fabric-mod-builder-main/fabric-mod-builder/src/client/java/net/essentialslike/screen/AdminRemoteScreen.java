package net.essentialslike.screen;

import net.essentialslike.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * 管理者リモコン GUI。
 * タブ構成：
 *   [採掘] 鉱石・石・砂ブレーカー
 *   [ツリー] ツリー・リーブス・苗木再植え付け
 *   [その他] 危険設定・速度制限・連鎖上限
 */
public final class AdminRemoteScreen extends Screen {
    private static final int TAB_MINING = 0;
    private static final int TAB_TREE   = 1;
    private static final int TAB_MISC   = 2;

    private ModConfig cfg;
    private int activeTab = TAB_MINING;

    public AdminRemoteScreen(ModConfig cfg) {
        super(Text.translatable("essentials_like.screen.admin_remote.title"));
        this.cfg = cfg;
    }

    public void refreshFromConfig(ModConfig cfg) {
        this.cfg = cfg;
        clearChildren();
        init();
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int top = 24;

        // タブボタン
        addDrawableChild(tabBtn(cx - 150, top, "essentials_like.screen.tab.mining", TAB_MINING));
        addDrawableChild(tabBtn(cx - 48,  top, "essentials_like.screen.tab.tree",   TAB_TREE));
        addDrawableChild(tabBtn(cx + 56,  top, "essentials_like.screen.tab.misc",   TAB_MISC));

        int y = top + 28;
        ModConfig.Global g = cfg.global;
        ModConfig.Player p = cfg.player;

        switch (activeTab) {
            case TAB_MINING -> {
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y,     "essentials_like.feature.ore_breaker",   g.oreBreaker,   "oreBreaker"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+26,  "essentials_like.feature.stone_breaker", g.stoneBreaker, "stoneBreaker"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+52,  "essentials_like.feature.sand_breaker",  g.sandBreaker,  "sandBreaker"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+78,  "essentials_like.setting.obsidian_stone","g.obsidianInStoneBreaker","obsidianInStoneBreaker"));
                // 個別設定（プレイヤー）
                addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+114, "essentials_like.setting.ore_invert_sneak",   p.oreInvertSneak,   "oreInvertSneak"));
                addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+140, "essentials_like.setting.stone_invert_sneak", p.stoneInvertSneak, "stoneInvertSneak"));
                addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+166, "essentials_like.setting.sand_invert_sneak",  p.sandInvertSneak,  "sandInvertSneak"));
                addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+192, "essentials_like.setting.sand_gravel",        p.sandGravel,       "sandGravel"));
                addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+218, "essentials_like.setting.sand_soul",          p.sandSoulSandAndSoil, "sandSoulSandAndSoil"));
                RemoteWidget.addIntPlayer(this, cx - 110, y+244, "essentials_like.setting.ore_radius",    p.oreBreakerRadius, 1, 8, 1, "oreBreakerRadius");
                RemoteWidget.addIntPlayer(this, cx - 110, y+270, "essentials_like.setting.breaker_w",     p.breakerSizeW,     1, 3, 1, "breakerSizeW");
                RemoteWidget.addIntPlayer(this, cx - 110, y+296, "essentials_like.setting.breaker_h",     p.breakerSizeH,     1, 3, 1, "breakerSizeH");
                RemoteWidget.addIntPlayer(this, cx - 110, y+322, "essentials_like.setting.breaker_d",     p.breakerSizeD,     1, 3, 1, "breakerSizeD");
            }
            case TAB_TREE -> {
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y,     "essentials_like.feature.tree_breaker",    g.treeBreaker,            "treeBreaker"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+26,  "essentials_like.feature.leaves_breaker",  g.leavesBreaker,          "leavesBreaker"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+52,  "essentials_like.feature.tree_replanting", g.treeReplanting,         "treeReplanting"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+78,  "essentials_like.setting.tree_fixed_dur",  g.treeBreakerFixedDurability,"treeBreakerFixedDurability"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+104, "essentials_like.setting.tree_limitless",  g.treeBreakerLimitless,   "treeBreakerLimitless"));
                addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+140, "essentials_like.setting.tree_invert_sneak",p.treeInvertSneak,        "treeInvertSneak"));
            }
            case TAB_MISC -> {
                // 速度・連鎖上限
                RemoteWidget.addIntGlobal(this, cx - 110, y,    "essentials_like.setting.speed_limit",   g.breakerSpeedLimit,          0,  20, 1, "breakerSpeedLimit");
                RemoteWidget.addIntGlobal(this, cx - 110, y+26, "essentials_like.setting.max_chain",     g.maxChainBlocks,             1,4096,64, "maxChainBlocks");
                RemoteWidget.addIntGlobal(this, cx - 110, y+52, "essentials_like.setting.death_timeout", g.deathWaypointTimeoutMinutes,20, 600, 10,"deathWaypointTimeoutMinutes");
                // 危険設定
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+88,  "essentials_like.setting.disenchanter_curse", g.disenchanterRemovesCurse,"disenchanterRemovesCurse"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+114, "essentials_like.setting.lava_reflow",        g.lavaReflow,              "lavaReflow"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+140, "essentials_like.setting.breakable_spawner",  g.breakableSpawner,        "breakableSpawner"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+166, "essentials_like.setting.private_deathpoint", g.privateDeathpoint,       "privateDeathpoint"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+192, "essentials_like.setting.lockable_chest",     g.lockableChest,           "lockableChest"));
                addDrawableChild(RemoteWidget.toggleGlobal(cx - 110, y+218, "essentials_like.setting.chunk_border_hud",   g.chunkBorderHud,          "chunkBorderHud"));
            }
        }

        // 閉じるボタン
        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"),
                btn -> onClose()).dimensions(cx - 50, height - 28, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx);
        // タイトルと区切り線
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 8, 0xFFFFDD);
        // 管理者設定ラベル
        ctx.drawTextWithShadow(textRenderer,
            Text.translatable("essentials_like.screen.admin_section"), width / 2 - 110, 54, 0xFFAA00);
        // 個別設定ラベル（採掘タブのみ）
        if (activeTab == TAB_MINING) {
            ctx.drawTextWithShadow(textRenderer,
                Text.translatable("essentials_like.screen.player_section"), width / 2 - 110, 136, 0xAAFFAA);
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }

    private ButtonWidget tabBtn(int x, int y, String key, int tab) {
        return ButtonWidget.builder(Text.translatable(key), btn -> {
            activeTab = tab;
            clearChildren();
            init();
        }).dimensions(x, y, 96, 20).build();
    }
}

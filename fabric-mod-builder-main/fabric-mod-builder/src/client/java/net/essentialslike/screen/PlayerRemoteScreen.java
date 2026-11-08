package net.essentialslike.screen;

import net.essentialslike.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * プレイヤー個別設定リモコン GUI。誰でも開ける。
 * 採掘ブレーカーのシフト反転・範囲・砂設定を変更できる。
 */
public final class PlayerRemoteScreen extends Screen {
    private ModConfig cfg;

    public PlayerRemoteScreen(ModConfig cfg) {
        super(Text.translatable("essentials_like.screen.player_remote.title"));
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
        int y = 36;
        ModConfig.Player p = cfg.player;

        // --- シフト反転 ---
        addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y,     "essentials_like.setting.ore_invert_sneak",   p.oreInvertSneak,      "oreInvertSneak"));
        addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+26,  "essentials_like.setting.stone_invert_sneak", p.stoneInvertSneak,    "stoneInvertSneak"));
        addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+52,  "essentials_like.setting.sand_invert_sneak",  p.sandInvertSneak,     "sandInvertSneak"));
        addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+78,  "essentials_like.setting.tree_invert_sneak",  p.treeInvertSneak,     "treeInvertSneak"));

        // --- 採掘範囲 ---
        RemoteWidget.addIntPlayer(this, cx - 110, y+114, "essentials_like.setting.ore_radius", p.oreBreakerRadius, 1, 8, 1, "oreBreakerRadius");
        RemoteWidget.addIntPlayer(this, cx - 110, y+140, "essentials_like.setting.breaker_w",  p.breakerSizeW,     1, 3, 1, "breakerSizeW");
        RemoteWidget.addIntPlayer(this, cx - 110, y+166, "essentials_like.setting.breaker_h",  p.breakerSizeH,     1, 3, 1, "breakerSizeH");
        RemoteWidget.addIntPlayer(this, cx - 110, y+192, "essentials_like.setting.breaker_d",  p.breakerSizeD,     1, 3, 1, "breakerSizeD");

        // --- 砂系の対象 ---
        addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+228, "essentials_like.setting.sand_gravel", p.sandGravel,          "sandGravel"));
        addDrawableChild(RemoteWidget.togglePlayer(cx - 110, y+254, "essentials_like.setting.sand_soul",   p.sandSoulSandAndSoil, "sandSoulSandAndSoil"));

        addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"),
                btn -> onClose()).dimensions(cx - 50, height - 28, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 8, 0xAAFFAA);
        ctx.drawTextWithShadow(textRenderer, Text.translatable("essentials_like.screen.sneak_section"),
                width / 2 - 110, 26, 0xCCCCCC);
        ctx.drawTextWithShadow(textRenderer, Text.translatable("essentials_like.screen.range_section"),
                width / 2 - 110, 100, 0xCCCCCC);
        ctx.drawTextWithShadow(textRenderer, Text.translatable("essentials_like.screen.sand_section"),
                width / 2 - 110, 214, 0xCCCCCC);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}

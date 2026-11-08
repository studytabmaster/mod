package net.essentialslike.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.essentialslike.network.Packets;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * リモコン GUI で使うウィジェットのファクトリ。
 * サーバーへのパケット送信もここで行う。
 */
public final class RemoteWidget {
    private RemoteWidget() {}

    /** boolean のトグルボタン（ON/OFFのラベルが切り替わる）。 */
    public static ButtonWidget toggleGlobal(int x, int y, String labelKey, boolean current, String field) {
        ButtonWidget[] ref = new ButtonWidget[1];
        ref[0] = ButtonWidget.builder(
                Text.translatable(labelKey).append(": ").append(Text.literal(current ? "§aON" : "§cOFF")),
                btn -> {
                    boolean next = btn.getMessage().getString().endsWith("ON");
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeString(field);
                    buf.writeBoolean(!next);
                    ClientPlayNetworking.send(Packets.C2S_SET_GLOBAL_BOOL, buf);
                    btn.setMessage(
                        Text.translatable(labelKey).append(": ").append(Text.literal(!next ? "§aON" : "§cOFF")));
                })
                .dimensions(x, y, 220, 20)
                .build();
        return ref[0];
    }

    public static ButtonWidget togglePlayer(int x, int y, String labelKey, boolean current, String field) {
        ButtonWidget[] ref = new ButtonWidget[1];
        ref[0] = ButtonWidget.builder(
                Text.translatable(labelKey).append(": ").append(Text.literal(current ? "§aON" : "§cOFF")),
                btn -> {
                    boolean next = btn.getMessage().getString().endsWith("ON");
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeString(field);
                    buf.writeBoolean(!next);
                    ClientPlayNetworking.send(Packets.C2S_SET_PLAYER_BOOL, buf);
                    btn.setMessage(
                        Text.translatable(labelKey).append(": ").append(Text.literal(!next ? "§aON" : "§cOFF")));
                })
                .dimensions(x, y, 220, 20)
                .build();
        return ref[0];
    }

    /** int スライダー代わりの ± ボタンペア（グローバル設定用）。 */
    public static void addIntGlobal(net.minecraft.client.gui.screen.Screen screen,
                                     int x, int y, String labelKey, int current, int min, int max, int step, String field) {
        int[] val = {current};
        ButtonWidget label = ButtonWidget.builder(
                intLabel(labelKey, current),
                btn -> {}).dimensions(x, y, 160, 20).build();
        label.active = false;

        ButtonWidget minus = ButtonWidget.builder(Text.literal("−"), btn -> {
            val[0] = Math.max(min, val[0] - step);
            label.setMessage(intLabel(labelKey, val[0]));
            sendInt(field, val[0], Packets.C2S_SET_GLOBAL_INT);
        }).dimensions(x + 164, y, 24, 20).build();

        ButtonWidget plus = ButtonWidget.builder(Text.literal("+"), btn -> {
            val[0] = Math.min(max, val[0] + step);
            label.setMessage(intLabel(labelKey, val[0]));
            sendInt(field, val[0], Packets.C2S_SET_GLOBAL_INT);
        }).dimensions(x + 190, y, 24, 20).build();

        screen.addDrawableChild(label);
        screen.addDrawableChild(minus);
        screen.addDrawableChild(plus);
    }

    public static void addIntPlayer(net.minecraft.client.gui.screen.Screen screen,
                                     int x, int y, String labelKey, int current, int min, int max, int step, String field) {
        int[] val = {current};
        ButtonWidget label = ButtonWidget.builder(
                intLabel(labelKey, current),
                btn -> {}).dimensions(x, y, 160, 20).build();
        label.active = false;

        ButtonWidget minus = ButtonWidget.builder(Text.literal("−"), btn -> {
            val[0] = Math.max(min, val[0] - step);
            label.setMessage(intLabel(labelKey, val[0]));
            sendInt(field, val[0], Packets.C2S_SET_PLAYER_INT);
        }).dimensions(x + 164, y, 24, 20).build();

        ButtonWidget plus = ButtonWidget.builder(Text.literal("+"), btn -> {
            val[0] = Math.min(max, val[0] + step);
            label.setMessage(intLabel(labelKey, val[0]));
            sendInt(field, val[0], Packets.C2S_SET_PLAYER_INT);
        }).dimensions(x + 190, y, 24, 20).build();

        screen.addDrawableChild(label);
        screen.addDrawableChild(minus);
        screen.addDrawableChild(plus);
    }

    private static Text intLabel(String key, int v) {
        return Text.translatable(key).append(": §e" + v);
    }

    private static void sendInt(String field, int value, Identifier packetId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(field);
        buf.writeInt(value);
        ClientPlayNetworking.send(packetId, buf);
    }
}

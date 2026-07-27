package net.dappls.legacy_utils.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/** Client-side chat output. All messages are local; nothing is ever sent to the server. */
public final class ChatUtils {

    private static final String PREFIX = "§8[§6Legacy§8] §r";

    private ChatUtils() {
    }

    /** Sends a raw, unprefixed message. */
    public static void send(String message) {
        if (message == null) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        // false = show in the chat hud rather than the action bar.
        client.player.sendMessage(Text.literal(message), false);
    }

    /** Normal feedback, e.g. "solver started". */
    public static void info(String message) {
        send(PREFIX + message);
    }

    /** Something the player should act on, e.g. "you are too far away". */
    public static void warn(String message) {
        send(PREFIX + "§e" + message);
    }

    /** Something went wrong inside the mod. */
    public static void error(String message) {
        send(PREFIX + "§c" + message);
    }
}

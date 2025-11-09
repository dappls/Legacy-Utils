package net.dappls.legacy_utils.client.Util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtils {
    public static void sendClientMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            // The second parameter 'false' means the message is not added to the system log
            client.player.sendMessage(Text.literal(message), false);
        }
    }
}

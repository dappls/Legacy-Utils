package net.dappls.legacy_utils.client.Util;

import net.dappls.legacy_utils.client.GUI.ModMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RegisterKeyBinds {
    private static KeyBinding openMenu;
    private static KeyBinding openIngenuityMenu;

    public static void init() {
        openMenu = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.Ingenuity.open_menu",   // translation key for the keybind
                        InputUtil.Type.KEYSYM,         // keyboard key
                        GLFW.GLFW_KEY_K,               // default key = K
                        "category.Ingenuity"         // translation key for category
                )
        );
        // Listen for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                client.setScreen(new ModMenu());
            }
        });
    }
}
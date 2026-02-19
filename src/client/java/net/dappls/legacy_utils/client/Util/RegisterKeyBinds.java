package net.dappls.legacy_utils.client.Util;

import net.dappls.legacy_utils.Legacy_utils;
import net.dappls.legacy_utils.client.GUI.ModMenu;
import net.dappls.legacy_utils.client.Legacy_utilsClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class RegisterKeyBinds {
    private static KeyBinding openMenu;
    private static KeyBinding openIngenuityMenu;

    public static void init() {
        openMenu = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.Ingenuity.open_menu",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_K,
                        KeyBinding.Category.create(Identifier.of("legacy_utils","ingenuity"))
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                client.setScreen(new ModMenu());
            }
        });
    }
}
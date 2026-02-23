package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Ithil.IthilSolver;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class IthilMenu extends AbstractLegacyGUI {

    // Chat toggle only (no solver state needed)
    public static boolean chatEnabled = true;

    public IthilMenu() {
        super(Text.literal("Ithil Solver Menu"));
    }

    @Override
    protected void setupGUI() {
        this.addLine("Welcome to the " + this.title.getString() + "!", 0xFFB38CFF);
        this.addLine(null);
        this.addLine("Renders correct Ithil pressure plates once.");
        this.addLine("This is a visual-only client-side effect.");
        this.addLine("Use Chat toggle to silence messages.");

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int totalHeight = 2 * buttonHeight + spacing;

        int startY = (this.height - totalHeight) / 2 + 60;
        int centerX = (this.width - buttonWidth) / 2;

        // --- Run Solver (One-shot render) ---
        ButtonWidget runSolver = ButtonWidget.builder(
                Text.literal("Render Plates"),
                (button) -> {
                    if (client != null && client.world != null) {
                        if (MinecraftClient.getInstance().player != null && (MinecraftClient.getInstance().player.getBlockPos().isWithinDistance(new BlockPos(-11225, 32, 12740), 100))) {
                            IthilSolver.showGoldenPlates();
                            if (chatEnabled) {
                                ChatUtils.sendClientMessage("Ithil plates rendered.");
                            }
                        }
                        else {
                            ChatUtils.sendClientMessage("You are not close enough to the puzzle");
                        }


                    }
                }
        ).dimensions(centerX, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(runSolver);

        // --- Toggle Chat ---
        ButtonWidget toggleChat = ButtonWidget.builder(
                Text.literal("Chat: " + (chatEnabled ? "ON" : "OFF")),
                (button) -> {
                    chatEnabled = !chatEnabled;
                    button.setMessage(Text.literal("Chat: " + (chatEnabled ? "ON" : "OFF")));
                }
        ).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build();
        this.addDrawableChild(toggleChat);
    }
}
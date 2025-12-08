package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Ingenuity.FindCharPlacement;
import net.dappls.legacy_utils.client.Ingenuity.PuzzleSolver;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class IngenuityMenu extends AbstractLegacyGUI {

    // Toggle state remains static here
    public static boolean chatEnabled = true;

    public IngenuityMenu() {
        super(Text.literal("Ingenuity Menu"));
    }

    @Override
    protected void setupGUI() {
        this.addLine("Welcome to the " + this.title.getString() + "!", 0x68D8D6);
        this.addLine(null);
        this.addLine("Click 'Run Solver' to calculate the solution.");
        this.addLine("Use the Chat toggle to silence solver output.");


        // --- Button Setup (Moved from init) ---
        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int totalHeight = 2 * buttonHeight + spacing;

        // Buttons should be anchored below the text lines. We calculate Y based on screen height
        int startY = (this.height - totalHeight) / 2 + 50;
        int centerX = (this.width - buttonWidth) / 2;

        // Run Solver button
        ButtonWidget runSolver = ButtonWidget.builder(Text.literal("Run Solver"), (button) -> {
            if (client != null && client.world != null) {
                List<String> solution = PuzzleSolver.solvePuzzle(
                        FindCharPlacement.getStartingValues(client.world)
                );

                if (solution == null) {
                    ChatUtils.sendClientMessage("Could not solve the puzzle.");
                }
            }
        }).dimensions(centerX, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(runSolver);

        // Toggle Chat button
        ButtonWidget toggleChat = ButtonWidget.builder(Text.literal("Chat: " + (chatEnabled ? "ON" : "OFF")), (button) -> {
            chatEnabled = !chatEnabled;
            // Update the button text immediately when clicked
            button.setMessage(Text.literal("Chat: " + (chatEnabled ? "ON" : "OFF")));
        }).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build();
        this.addDrawableChild(toggleChat);

    }

}
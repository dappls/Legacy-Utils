package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Ingenuity.FindCharPlacement;
import net.dappls.legacy_utils.client.Ingenuity.PuzzleSolver;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class IngenuityMenu extends Screen {
    public IngenuityMenu() {
        super(Text.literal("Ingenuity Menu"));
    }
    public static boolean chatEnabled = true; // toggle state
    @Override
    protected void init() {
        super.init();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int totalHeight = 2 * buttonHeight + spacing;
        int startY = (this.height - totalHeight) / 2;
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

        ButtonWidget toggleChat = ButtonWidget.builder(Text.literal("Chat: " + (chatEnabled ? "ON" : "OFF")), (button) -> {
            chatEnabled = !chatEnabled;
            button.setMessage(Text.literal("Chat: " + (chatEnabled ? "ON" : "OFF")));
        }).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build();
        this.addDrawableChild(toggleChat);

        // Back button
        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"), (button) -> MinecraftClient.getInstance().setScreen(new ModMenu())).dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, "Ingenuity Puzzle Solver", this.width / 2, 40, 0xFFFFFF);
        int textY = 60;
        context.drawCenteredTextWithShadow(this.textRenderer, "Then click 'Run Solver' to calculate the solution.", this.width / 2, textY, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer, "Use the Chat toggle to silence solver output.", this.width / 2, textY + 12, 0xFFAA00);

        super.render(context, mouseX, mouseY, delta);
    }
}

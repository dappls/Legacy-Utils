package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.LightsOut.BoardMatrix;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class LightsOutMenu extends AbstractLegacyGUI {

    public LightsOutMenu() {
        super(Text.literal("Lights Menu"));
    }

    @Override
    protected void setupGUI() {
        this.addLine("Welcome to the " + this.title.getString() + "!", 0xFFeb6424);
        this.addLine(null);
        this.addLine("Press the desired button to solve the puzzle");

        // --- Button Setup (Moved from init) ---
        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int totalHeight = 3 * buttonHeight + 2 * spacing;

        // Anchor buttons below the static text
        int startY = (this.height - totalHeight) / 2 + 30;
        int centerX = (this.width - buttonWidth) / 2;

        // Initialize BoardMatrix here, as it's needed for button logic
        BoardMatrix board = new BoardMatrix();
        board.init();

        ButtonWidget lit = ButtonWidget.builder(Text.literal("Anaur (Light)"), button -> {
            if (board.isPlayerAtBoard()) board.renderSolution(1);
        }).dimensions(centerX, startY, buttonWidth, buttonHeight).build();

        ButtonWidget unlit = ButtonWidget.builder(Text.literal("Ithil (Dark)"), button -> {
            if (board.isPlayerAtBoard()) board.renderSolution(0);
        }).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build();

        ButtonWidget centerLit = ButtonWidget.builder(Text.literal("Strength Trial"), button -> {
            if (board.isPlayerAtBoard()) board.renderSolution(2);
        }).dimensions(centerX, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight).build();

        this.addDrawableChild(lit);
        this.addDrawableChild(unlit);
        this.addDrawableChild(centerLit);

    }
}
package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.LightsOut.BoardMatrix;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class LightsOutMenu extends Screen {

    public LightsOutMenu() {
        super(Text.literal("Lights Menu"));
    }

    @Override
    protected void init() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int totalHeight = 3 * buttonHeight + 2 * spacing;
        int startY = (this.height - totalHeight) / 2;
        int centerX = (this.width - buttonWidth) / 2;

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

        // Back button
        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"), (button) -> MinecraftClient.getInstance().setScreen(new ModMenu())).dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);
        int yStart = 50;
        context.drawCenteredTextWithShadow(this.textRenderer,
                "",
                this.width / 2, yStart, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "This is the LightsOut solver!",
                this.width / 2, yStart+10, 0xFFAA00);

        context.drawCenteredTextWithShadow(this.textRenderer,
                "Press the desired button to solve the puzzle",
                this.width / 2, yStart+20, 0xFFAA00);
        super.render(context, mouseX, mouseY, delta);
    }
}

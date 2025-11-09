package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Honey.HoneySolver;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class HoneyMenu extends Screen {

    public HoneyMenu() {
        super(Text.literal("Honey Menu"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);

        int yStart = 50;
        context.drawCenteredTextWithShadow(this.textRenderer,
                "",
                this.width / 2, yStart, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "This is the honey solver!",
                this.width / 2, yStart+10, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Press the button and navigate to the pressure plate",
                this.width / 2, yStart+20, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Go back to the center and repeat until all lights are dimmed",
                this.width / 2, yStart+30, 0xFFAA00);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int numButtons = 3;
        int totalHeight = numButtons * buttonHeight + (numButtons - 1) * spacing;
        int startY = (this.height - totalHeight) / 2;
        int centerX = (this.width - buttonWidth) / 2;

        // Run Solver button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Run Solver"), button -> {
            if (MinecraftClient.getInstance().player != null) {
                HoneySolver.solveToCandle(MinecraftClient.getInstance().player.getBlockPos());
            }
        }).dimensions(centerX, startY, buttonWidth, buttonHeight).build());

        // Remove Trail button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Remove Trail"), button -> {
            HoneySolver.trailActive = false;
            HoneySolver.solvedPath.clear();
            ChatUtils.sendClientMessage("Rainbow trail removed");
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.closeScreen();
            }
        }).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build());

        // Back button
        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"), (button) -> MinecraftClient.getInstance().setScreen(new ModMenu())).dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);
    }

}

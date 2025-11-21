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
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class HoneyMenu extends Screen {

    public HoneyMenu() {
        super(Text.literal("Honey Menu"));
    }


    @Override
    protected void init() {

        super.init();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int numButtons = 2;
        int totalHeight = numButtons * buttonHeight + (numButtons - 1) * spacing;
        int startY = (this.height - totalHeight) / 2;
        int centerX = (this.width - buttonWidth) / 2;

        // ===========================
        // ⚡ HONEY SOLVER TOGGLE
        // ===========================

        String toggleText = HoneySolver.honeyActive ?
                "Disable Honey Solver" :
                "Enable Honey Solver";

        this.addDrawableChild(ButtonWidget.builder(Text.literal(toggleText), button -> {
            BlockPos firstPos = new BlockPos(9763, 55, 52356); // You could also pick LEVER_B or LEVER_C; just pick one
            if (MinecraftClient.getInstance().player != null && !MinecraftClient.getInstance().player.getBlockPos().isWithinDistance(firstPos, 700)) {
                ChatUtils.sendClientMessage("Not within range of puzzle!");
                return; // Stop further updates if out of range
            }
            HoneySolver.honeyActive = !HoneySolver.honeyActive;

            if (HoneySolver.honeyActive) {
                ChatUtils.sendClientMessage("Honey Solver Enabled");

                if (MinecraftClient.getInstance().player != null) {
                    HoneySolver.startSolving(MinecraftClient.getInstance().player.getBlockPos());
                }

            } else {
                HoneySolver.disableHoney();
                ChatUtils.sendClientMessage("Honey Solver Disabled");
            }

            // Refresh text
            this.clearAndInit();
        }).dimensions(centerX, startY, buttonWidth, buttonHeight).build());

        // ===========================
        // 🧹 REMOVE TRAIL BUTTON
        // ===========================

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Remove Trail"), button -> {
            HoneySolver.trailActive = false;
            HoneySolver.solvedPath.clear();
            ChatUtils.sendClientMessage("Rainbow trail removed");

            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.closeScreen();
            }
        }).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build());

        // Back button
        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"),
                        b -> MinecraftClient.getInstance().setScreen(new ModMenu()))
                .dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);

        int yStart = 50;
        context.drawCenteredTextWithShadow(this.textRenderer, "This is the honey solver!", this.width / 2, yStart + 10, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer, "Press toggle and step on the lit plate", this.width / 2, yStart + 20, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer, "Return to center and repeat", this.width / 2, yStart + 30, 0xFFAA00);

        // Display state
        String state = HoneySolver.honeyActive ? "Status: ACTIVE" : "Status: OFF";
        context.drawCenteredTextWithShadow(this.textRenderer, state, this.width / 2, yStart + 50,
                HoneySolver.honeyActive ? 0x00FF00 : 0xFF4444);

        super.render(context, mouseX, mouseY, delta);
    }
}

package net.dappls.legacy_utils.client.GUI;


import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.dappls.legacy_utils.client.Honey.HoneySolver.disableHoney;
import static net.dappls.legacy_utils.client.Honey.HoneySolver.honeyActive;

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
        // Increased number of buttons to 3 to accommodate the new "Return Path" button
        int numButtons = 3;
        int totalHeight = numButtons * buttonHeight + (numButtons - 1) * spacing;
        int startY = (this.height - totalHeight) / 2;
        int centerX = (this.width - buttonWidth) / 2;

        // ===========================
        // 1. HONEY SOLVER / NEXT CANDLE
        // ===========================



        this.addDrawableChild(ButtonWidget.builder(Text.literal("Toggle Honey Solver"), button -> {
            BlockPos firstPos = new BlockPos(9763, 55, 52356);
            if (MinecraftClient.getInstance().player != null && !MinecraftClient.getInstance().player.getBlockPos().isWithinDistance(firstPos, 100)) {
                ChatUtils.sendClientMessage("Not within range of puzzle!");
                disableHoney();
                return;
            }


            if (!honeyActive) {
                honeyActive = true;
            } else {
                disableHoney();
                return;
            }
            this.clearAndInit();
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

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);

        // Status line
        String state = honeyActive ? "Status: ACTIVE" : "Status: OFF";
        int stateColor = honeyActive ? 0x00FF00 : 0xFF4444;
        context.drawCenteredTextWithShadow(this.textRenderer, state, this.width / 2, 60, stateColor);


        int amber = 0xFFAA00;
        context.drawCenteredTextWithShadow(this.textRenderer, "This is the Honey solver!", this.width / 2, 80, amber);
        context.drawCenteredTextWithShadow(this.textRenderer, "Toggle the solver on to solve all candles", this.width / 2, 100, amber);
        context.drawCenteredTextWithShadow(this.textRenderer, "Turn the solver off before clicking the final button", this.width / 2, 120, amber);

        super.render(context, mouseX, mouseY, delta);
    }

}
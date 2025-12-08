package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.dappls.legacy_utils.client.Honey.HoneySolver.disableHoney;
import static net.dappls.legacy_utils.client.Honey.HoneySolver.honeyActive;

@Environment(EnvType.CLIENT)
public class HoneyMenu extends AbstractLegacyGUI {

    private static final int STATUS_LINE_Y = 120;

    public HoneyMenu() {
        super(Text.literal("Honey Menu"));
    }

    @Override
    protected void setupGUI() {
        this.addLine("Welcome to the " + this.title.getString() + "!", 0xEBA937);
        this.addLine(null);
        this.addLine("Toggle the solver on to solve all candles"); // Line 2
        this.addLine("Turn the solver off before clicking the final button"); // Line 3

        // Note: The Y coordinates will now be based on the AbstractGUI's LINE_HEIGHT

        // --- Button Setup (Moved from init to setupGUI) ---
        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int numButtons = 3;
        int totalHeight = numButtons * buttonHeight + (numButtons - 1) * spacing;
        int startY = (this.height - totalHeight) / 2;
        int centerX = (this.width - buttonWidth) / 2;

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
            }
            this.clearAndInit();
        }).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build());

    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String state = honeyActive ? "Status: ACTIVE" : "Status: OFF";
        int stateColor = honeyActive ? 0x00FF00 : 0xFF4444;

        context.drawCenteredTextWithShadow(this.textRenderer, state, this.width / 2, STATUS_LINE_Y, stateColor);
    }
}
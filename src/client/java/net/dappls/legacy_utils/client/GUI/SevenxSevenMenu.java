package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.SevenxSeven.GlowingOutlineRenderer;
import net.dappls.legacy_utils.client.SevenxSeven.SevenxSevenMatrix;
import net.dappls.legacy_utils.client.SevenxSeven.SevenxSevenPositions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class SevenxSevenMenu extends Screen {
    private static final int GRID_SIZE = 7;
    private static final int BUTTON_SIZE = 20;
    private static final int SPACING = 4;

    private final BlockPosButton[][] buttons = new BlockPosButton[GRID_SIZE][GRID_SIZE];
    private final Map<Integer, BlockPosButton> buttonMap = new HashMap<>();

    public SevenxSevenMenu() {

        super(Text.literal("7x7 Menu"));
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);

        int yStart = 50;
        int lineHeight = 12;
        context.drawCenteredTextWithShadow(this.textRenderer,
                "",
                this.width / 2, yStart, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "This is the 7x7 solver!",
                this.width / 2, yStart+lineHeight, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Input values from deepslate onto GUI grid",
                this.width / 2, yStart+lineHeight*2, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Orient yourself by using the Start grid, which refers to the yellow square.", this.width / 2, yStart + lineHeight*3, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Screenshot the map and put the points into the grid", this.width / 2, yStart + lineHeight*4, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                " Start-> White     1-> Red     2 -> Yellow     3 -> Green     4 -> Blue", this.width / 2, yStart + lineHeight * 5, 0xFFAA00);

        super.render(context, mouseX, mouseY, delta);
    }

    private static class BlockPosButton extends ButtonWidget {
        private int displayValue;
        private final boolean isStart;
        private final BlockPos pos;

        public BlockPosButton(int x, int y, int width, int height, boolean isStart, BlockPos pos,
                              NarrationSupplier narration) {
            super(x, y, width, height, Text.literal(isStart ? "Start" : ""), b -> {}, narration);
            this.isStart = isStart;
            this.pos = pos;
            this.displayValue = 0;
            updateText();
        }

        @Override
        public void onPress() {
            if (!isStart) {
                displayValue = (displayValue + 1) % 5;
                updateText();
            }
        }

        private void updateText() {
            this.setMessage(isStart ? Text.literal("Start")
                    : displayValue == 0 ? Text.literal("")
                    : Text.literal(Integer.toString(displayValue)));
        }
        public BlockPos getBlockPos() { return pos; }
        public int getDisplayValue() { return displayValue; }

    }

    private void createAllButtons() {
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            int key = i + 1;
            boolean isStart = key == 39;
            BlockPos pos = SevenxSevenPositions.BLOCK_POS_LIST[i];

            BlockPosButton button = new BlockPosButton(
                    0, 0, BUTTON_SIZE, BUTTON_SIZE,
                    isStart, pos, b -> Text.literal("")
            );

            buttonMap.put(key, button);
            this.addDrawableChild(button);
        }
    }

    private void generateTable() {
        int gridWidth = GRID_SIZE * (BUTTON_SIZE + SPACING) - SPACING;
        int startX = (this.width - gridWidth) / 2;
        int startY = 130;


        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int key = SevenxSevenMatrix.matrix7x7[row][col];
                BlockPosButton button = buttonMap.get(key);
                if (button == null) continue;

                button.setX(startX + col * (BUTTON_SIZE + SPACING));
                button.setY(startY + row * (BUTTON_SIZE + SPACING));

                buttons[row][col] = button;
            }
        }
    }

    @Override
    protected void init() {
        createAllButtons();
        generateTable();
        int buttonWidth = 80;
        int buttonHeight = 20;
        int centerX = this.width / 2;
        int solveY = 130 + GRID_SIZE * (BUTTON_SIZE + SPACING) + SPACING;
        int spacingBetweenButtons = 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Rotate"), b -> {
                    SevenxSevenMatrix.rotateMatrix90Clockwise();
                    MinecraftClient.getInstance().setScreen(new SevenxSevenMenu());
                })
                .dimensions(centerX - buttonWidth - spacingBetweenButtons, solveY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Solve"), b -> solveGrid())
                .dimensions(centerX + spacingBetweenButtons, solveY, buttonWidth, buttonHeight).build());

        int clearY = solveY + buttonHeight + (spacingBetweenButtons + 7);
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Clear Outlines"), b -> GlowingOutlineRenderer.ClearRendering())
                .dimensions(centerX - buttonWidth / 2, clearY, buttonWidth, buttonHeight).build());

        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"), (button) -> MinecraftClient.getInstance().setScreen(new ModMenu()))
                .dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);
    }




    private void solveGrid() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        GlowingOutlineRenderer.clearAll();

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                BlockPosButton btn = buttons[y][x];
                if (btn == null) continue;

                int value = btn.getDisplayValue();
                BlockPos pos = btn.getBlockPos();
                if (btn.isStart) {
                    GlowingOutlineRenderer.AddRenderPosition(SevenxSevenPositions.BLOCK_POS_LIST[38], 1f, 1f, 1f, 1f);
                    continue;
                }
                switch (value) {
                    case 1 -> GlowingOutlineRenderer.AddRenderPosition(pos, 1f, 0f, 0f, 1f);
                    case 2 -> GlowingOutlineRenderer.AddRenderPosition(pos, 1f, 1f, 0f, 1f);
                    case 3 -> GlowingOutlineRenderer.AddRenderPosition(pos, 0f, 1f, 0f, 1f);
                    case 4 -> GlowingOutlineRenderer.AddRenderPosition(pos, 0f, 0f, 1f, 1f);
                }
            }
        }
    }
}

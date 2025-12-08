package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.SevenxSeven.GlowingOutlineRenderer;
import net.dappls.legacy_utils.client.SevenxSeven.SevenxSevenMatrix;
import net.dappls.legacy_utils.client.SevenxSeven.SevenxSevenPositions;
import net.dappls.legacy_utils.client.SevenxSeven.LoreMatrix;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import java.util.Map;


public class SevenxSevenMenu extends AbstractLegacyGUI {
    private static final int GRID_SIZE = 7;
    private static final int BUTTON_SIZE = 20;
    private static final int SPACING = 4;
    public int rotation = 0;

    private final BlockPosButton[][] buttons = new BlockPosButton[GRID_SIZE][GRID_SIZE];
    private final Map<Integer, BlockPosButton> buttonMap = new HashMap<>();


    private static final int GRID_START_Y = 150;
    private static final int ROTATION_TEXT_LINE = 6;
    private static final int BODY_Y_START = 50;
    private static final int LINE_HEIGHT = 15;

    public SevenxSevenMenu() {
        super(Text.literal("7x7 Menu"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int rotationY = BODY_Y_START + (ROTATION_TEXT_LINE * LINE_HEIGHT);
        int rotationColor;
        if (rotation == 0) {
            rotationColor = 0x00FF00; // Bright Green for 0 degrees
        } else {
            rotationColor = 0xFF4444;     // Amber for any other rotation
        }
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Rotation: " + rotation + "°",
                this.width / 2, rotationY, rotationColor);
    }

    private static class BlockPosButton extends ButtonWidget {
        private int displayValue;
        private final BlockPos pos;
        private final int matrixRow; // Store matrix coordinates
        private final int matrixCol; // Store matrix coordinates

        public BlockPosButton(int x, int y, int width, int height, boolean isStart, BlockPos pos,
                              int row, int col, // New parameters
                              NarrationSupplier narration) {
            super(x, y, width, height, Text.literal(isStart ? "Start" : ""), b -> {}, narration);
            this.pos = pos;
            this.matrixRow = row;
            this.matrixCol = col;
            this.displayValue = 0;
            updateText();
        }

        @Override
        public void onPress() {
            // Cycle the display value (0 to 5)
            displayValue = (displayValue + 1) % 6;
            updateText();

            // --- FIX: Update the global matrix on button click ---
            if (SevenxSevenMatrix.matrix7x7 != null) {
                SevenxSevenMatrix.matrix7x7[this.matrixRow][this.matrixCol] = this.displayValue;
            }
        }
        private void updateText() {
            this.setMessage(displayValue == 0 ? Text.literal("")
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
            int row = i / GRID_SIZE;
            int col = i % GRID_SIZE;

            BlockPosButton button = new BlockPosButton(
                    0, 0, BUTTON_SIZE, BUTTON_SIZE,
                    isStart, pos, row,col,b -> Text.literal("")
            );

            buttonMap.put(key, button);
            this.addDrawableChild(button);
        }
    }


    private void generateTable() {
        int[][] matrix = SevenxSevenMatrix.matrix7x7;
        if (matrix == null) return;

        int gridWidth = GRID_SIZE * (BUTTON_SIZE + SPACING) - SPACING;
        int startX = (this.width - gridWidth) / 2;

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int key = row * GRID_SIZE + col + 1;
                BlockPosButton button = buttonMap.get(key);
                if (button == null) continue;

                button.setX(startX + col * (BUTTON_SIZE + SPACING));
                button.setY(GRID_START_Y + row * (BUTTON_SIZE + SPACING));

                button.displayValue = matrix[row][col];
                button.updateText();
                buttons[row][col] = button;

                // check for 5 and set rotation
                if (matrix[row][col] == 5) {
                    if (row == 5 && col == 3) rotation = 0;
                    else if (row == 3 && col == 1) rotation = 90;
                    else if (row == 1 && col == 3) rotation = 180;
                    else if (row == 3 && col == 5) rotation = 270;
                }
            }
        }
    }

    @Override
    protected void setupGUI() {
        this.addLine("Welcome to the " + this.title.getString() + "!", 0xffe6a7);

        this.addLine(null);
        this.addLine("Hold the item and press load to load the numbers into the grid"); // Line 4
        this.addLine("rotate until the rotation is 0°");
        this.addLine("On the rare chance a number is on the start tile, you MUST input the start tile and NOT the button");
        this.addLine("1-> Red     2 -> Yellow     3 -> Green     4 -> Blue     5(yellow tile)-> White");



        this.addLine(null);
        this.addLine(null);



        createAllButtons();
        generateTable();


        int buttonWidth = 80;
        int buttonHeight = 20;
        int spacing = 10;

        int centerX = this.width / 2;
        int centerY = GRID_START_Y + GRID_SIZE * (BUTTON_SIZE + SPACING) + SPACING + 20;

        int squareWidth = buttonWidth * 2 + spacing;
        int startX = centerX - squareWidth / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Load"), b -> {
                    int[][] parsed = LoreMatrix.getHeldItemMatrix();
                    if (parsed != null) {
                        SevenxSevenMatrix.matrix7x7 = parsed;
                        generateTable();
                    }
                })
                .dimensions(startX, centerY, buttonWidth, buttonHeight).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Rotate"), b -> {
                    SevenxSevenMatrix.rotateMatrix90Clockwise();
                    MinecraftClient.getInstance().setScreen(new SevenxSevenMenu());
                })
                .dimensions(startX + buttonWidth + spacing, centerY, buttonWidth, buttonHeight).build());


        this.addDrawableChild(ButtonWidget.builder(Text.literal("Solve"), b -> solveGrid())
                .dimensions(startX, centerY + buttonHeight + spacing, buttonWidth, buttonHeight).build());


        this.addDrawableChild(ButtonWidget.builder(Text.literal("Clear Outlines"), b -> GlowingOutlineRenderer.ClearRendering())
                .dimensions(startX + buttonWidth + spacing, centerY + buttonHeight + spacing, buttonWidth, buttonHeight).build());


        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"),
                button -> MinecraftClient.getInstance().setScreen(new ModMenu())).dimensions(10, 10, 20, 20).build());
    }

    private void solveGrid() {
        GlowingOutlineRenderer.clearAll();

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                BlockPosButton btn = buttons[y][x];
                if (btn == null) continue;

                int value = btn.getDisplayValue();
                BlockPos pos = btn.getBlockPos();

                switch (value) {
                    case 1 -> GlowingOutlineRenderer.AddRenderPosition(pos, 1f, 0f, 0f, 1f);
                    case 2 -> GlowingOutlineRenderer.AddRenderPosition(pos, 1f, 1f, 0f, 1f);
                    case 3 -> GlowingOutlineRenderer.AddRenderPosition(pos, 0f, 1f, 0f, 1f);
                    case 4 -> GlowingOutlineRenderer.AddRenderPosition(pos, 0f, 0f, 1f, 1f);
                    case 5 -> GlowingOutlineRenderer.AddRenderPosition(pos, 1f, 1f, 1f, 1f);
                }
            }
        }
    }
}
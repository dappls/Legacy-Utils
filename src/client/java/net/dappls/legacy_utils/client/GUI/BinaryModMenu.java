package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Binary.BinarySolver;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BinaryModMenu extends AbstractLegacyGUI {

    private TextFieldWidget binaryInput;
    private TextFieldWidget decimalInput;
    private final List<String> solutionLines = new ArrayList<>();

    public BinaryModMenu() {
        super(Text.literal("Binary Menu"));
    }

    @Override
    protected void setupGUI() {
        // --- Static Info Lines ---
        this.addLine("Welcome to the " + this.title.getString() + "!", 0xFF26547C);
        this.addLine(null);
        this.addLine("Binary Solver Instructions:");
        this.addLine("Enter number from paper to convert it to binary");
        this.addLine("Press Solve to solve the puzzle!");
        this.addLine(null); // Line 6: Gap for the dynamic rotation text


        int centerX = this.width / 2;
        // Adjusted startY to give space to the new static info lines (Y=74 to Y=98)
        int startY = 120;

        int labelWidth = 160;
        int fieldWidth = 100;
        int buttonWidth = 90;
        int spacing = 5;

        // X coordinate calculation (common for labels and fields)
        int xCoordinates = centerX - (labelWidth + spacing + fieldWidth) / 2;

        // --- 1. Decimal Input and Convert Button ---

        // Decimal input field
        decimalInput = new TextFieldWidget(this.textRenderer,
                xCoordinates + labelWidth + spacing,
                startY,
                fieldWidth,
                20,
                Text.literal(""));
        decimalInput.setMaxLength(10);
        this.addSelectableChild(decimalInput);

        // Convert Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Convert"),
                b -> {
                    String decText = decimalInput.getText().trim();
                    try {
                        int value = Integer.parseInt(decText);
                        if (value < 0) {
                            ChatUtils.sendClientMessage("Enter a positive integer!");
                            return;
                        }
                        String binaryStr = Integer.toBinaryString(value);
                        // Pad with leading zeros to 8 bits for typical puzzle format
                        binaryStr = String.format("%8s", binaryStr).replace(' ', '0');
                        binaryInput.setText(binaryStr);
                    } catch (NumberFormatException e) {
                        ChatUtils.sendClientMessage("Invalid decimal number!");
                    }
                }).dimensions(decimalInput.getX() + fieldWidth + spacing, startY, buttonWidth, 20).build());

        // --- 2. Binary Input and Solve Button ---
        startY += 30;

        // Binary input field
        binaryInput = new TextFieldWidget(this.textRenderer,
                xCoordinates + labelWidth + spacing,
                startY,
                fieldWidth,
                20,
                Text.literal(""));
        binaryInput.setMaxLength(32);
        this.addSelectableChild(binaryInput);

        // Solve Button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Solve"),
                b -> {
                    String text = binaryInput.getText().trim();
                    if (!text.matches("[01]+")) {
                        ChatUtils.sendClientMessage("Invalid input! Enter binary digits (0 or 1).");
                        return;
                    }
                    if (MinecraftClient.getInstance().player != null &&
                            !MinecraftClient.getInstance().player.getBlockPos().isWithinDistance(BinarySolver.LAMP_POS, 700)) {
                        ChatUtils.sendClientMessage("Not within range of puzzle!");
                        return;
                    }
                    List<Integer> input = new ArrayList<>();
                    for (char c : text.toCharArray()) input.add(c == '1' ? 1 : 0);
                    BinarySolver.startBinarySolve(input);
                }).dimensions(binaryInput.getX() + fieldWidth + spacing, startY, buttonWidth, 20).build());
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int labelWidth = 160;
        int spacing = 5;
        int labelX = this.width / 2 - (labelWidth + spacing + decimalInput.getWidth()) / 2;


        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Enter integer:"),
                labelX,
                decimalInput.getY() + 6,
                LAVENDER);
        decimalInput.render(context, mouseX, mouseY, delta);


        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Enter binary:"),
                labelX,
                binaryInput.getY() + 6,
                LAVENDER);
        binaryInput.render(context, mouseX, mouseY, delta);


        int instrY = binaryInput.getY() + 40;

        for (String line : solutionLines) {
            context.drawCenteredTextWithShadow(this.textRenderer, line, this.width / 2, instrY, 0xFF00FF00);
            instrY += 12;
        }

    }
}
package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Binary.BinarySolver;
import net.dappls.legacy_utils.client.Util.ChatUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BinaryModMenu extends Screen {

    private TextFieldWidget binaryInput;
    private final List<String> solutionLines = new ArrayList<>();
    private TextFieldWidget decimalInput;

    public BinaryModMenu() {
        super(Text.literal("Binary Menu"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = this.height / 2 - 20;

        int labelWidth = 160;  // width reserved for label
        int fieldWidth = 100;  // width of text field
        int buttonWidth = 90;  // width of button
        int spacing = 5;       // space between elements

        // Decimal input field and button
        decimalInput = new TextFieldWidget(this.textRenderer,
                centerX - (labelWidth + spacing + fieldWidth + spacing + buttonWidth) / 2 + labelWidth + spacing,
                startY,
                fieldWidth,
                20,
                Text.literal(""));
        decimalInput.setMaxLength(10);
        addSelectableChild(decimalInput);

        addDrawableChild(ButtonWidget.builder(Text.literal("Convert"),
                b -> {
                    String decText = decimalInput.getText().trim();
                    try {
                        int value = Integer.parseInt(decText);
                        if (value < 0) {
                            ChatUtils.sendClientMessage("Enter a positive integer!");
                            return;
                        }
                        String binaryStr = Integer.toBinaryString(value);
                        binaryStr = String.format("%8s", binaryStr).replace(' ', '0');
                        binaryInput.setText(binaryStr);
                    } catch (NumberFormatException e) {
                        ChatUtils.sendClientMessage("Invalid decimal number!");
                    }
                }).dimensions(decimalInput.getX() + fieldWidth + spacing, startY, buttonWidth, 20).build());

        // Binary input field and solve button
        startY += 30;
        binaryInput = new TextFieldWidget(this.textRenderer,
                centerX - (labelWidth + spacing + fieldWidth + spacing + buttonWidth) / 2 + labelWidth + spacing,
                startY,
                fieldWidth,
                20,
                Text.literal(""));
        binaryInput.setMaxLength(32);
        addSelectableChild(binaryInput);

        addDrawableChild(ButtonWidget.builder(Text.literal("Solve"),
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
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);

        int centerX = this.width / 2;
        int startY = this.height / 2 - 20;
        int labelWidth = 160; // same as init
        int spacing = 5;

        // Decimal label
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Enter integer:"),
                centerX - (labelWidth + spacing + decimalInput.getWidth()) / 2,
                startY + 6, // slightly vertically centered with field
                0xFFAA00);
        decimalInput.render(context, mouseX, mouseY, delta);

        // Binary label
        startY += 30;
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Enter binary:"),
                centerX - (labelWidth + spacing + binaryInput.getWidth()) / 2,
                startY + 6,
                0xFFAA00);
        binaryInput.render(context, mouseX, mouseY, delta);

        // Instructions below inputs/buttons
        int instrY = startY + 40;
        context.drawCenteredTextWithShadow(this.textRenderer, "Binary Solver Instructions:", centerX, 50, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer, "Enter number from paper to convert it to binary", centerX, 60, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer, "Press Solve to solve the puzzle!", centerX, 70, 0xFFAA00);
        instrY += 12;
        for (String line : solutionLines) {
            context.drawCenteredTextWithShadow(this.textRenderer, line, centerX, instrY, 0x00FF00);
            instrY += 12;
        }

        super.render(context, mouseX, mouseY, delta);
    }

}

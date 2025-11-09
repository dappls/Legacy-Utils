package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Binary.BinarySolver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BinaryModMenu extends Screen {

    private final List<ButtonWidget> bitButtons = new ArrayList<>();
    private final List<Integer> bitValues = new ArrayList<>();

    public BinaryModMenu() {
        super(Text.literal("Binary Menu"));
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
                "This is the Binary solver!",
                this.width / 2, yStart+10, 0xFFAA00);

        context.drawCenteredTextWithShadow(this.textRenderer,
                "Toggle the binary code and press the button to solve!",
                this.width / 2, yStart+20, 0xFFAA00);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        bitButtons.clear();
        bitValues.clear();

        int buttonSize = 25;
        int spacing = 5;
        int totalWidth = (buttonSize + spacing) * 8 - spacing;
        int startX = (this.width - totalWidth) / 2;
        int startY = this.height / 2 - 30;

        // 8 toggle buttons
        for (int i = 0; i < 8; i++) {
            int index = i;
            bitValues.add(0); // default 0

            ButtonWidget bitButton = ButtonWidget.builder(Text.literal("0"), (button) -> {
                int current = bitValues.get(index);
                int newValue = current == 0 ? 1 : 0;
                bitValues.set(index, newValue);

                // Update button text and color
                button.setMessage(Text.literal(String.valueOf(newValue))
                        .formatted(newValue == 1 ? Formatting.GREEN : Formatting.WHITE));
            }).dimensions(startX + i * (buttonSize + spacing), startY, buttonSize, buttonSize).build();

            this.addDrawableChild(bitButton);
            bitButtons.add(bitButton);
        }

        // Solve button
        ButtonWidget solveButton = ButtonWidget.builder(Text.literal("Solve Binary Puzzle"), (button) -> {

            List<Integer> playerInput = new ArrayList<>(bitValues); // GUI toggles
            List<String> solution = BinarySolver.solveBinaryPuzzle(playerInput);

        }).dimensions(this.width / 2 - 75, startY + 45, 150, 20).build();

        this.addDrawableChild(solveButton);

        // Back button
        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"), (button) -> MinecraftClient.getInstance().setScreen(new ModMenu())).dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);

        // Label
        this.addDrawableChild(new TextWidget(this.width / 2 - 60, startY - 25, 120, 20,
                Text.literal("Toggle the bits (0/1):"), this.textRenderer));
    }

}

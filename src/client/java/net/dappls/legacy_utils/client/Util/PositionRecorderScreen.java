package net.dappls.legacy_utils.client.Util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PositionRecorderScreen extends Screen {

    private static boolean recording = false;
    private static final List<String> recordedPositions = new ArrayList<>();
    private static boolean registered = false;
    private TextFieldWidget labelField;
    private static String sessionLabel = "Recorded Session";

    public PositionRecorderScreen() {
        super(Text.literal("Position Recorder"));
        if (!registered) {
            registerTickEvent();
            registered = true;
        }
    }

    @Override
    protected void init() {
        super.init();

        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 10;
        int totalHeight = 2 * buttonHeight + spacing;
        int startY = (this.height - totalHeight) / 2;
        int centerX = (this.width - buttonWidth) / 2;

        // Text field for custom label
        labelField = new TextFieldWidget(
                this.textRenderer,
                centerX - 50,
                startY - 40,
                200,
                20,
                Text.literal("Label")
        );
        labelField.setText(sessionLabel);
        this.addSelectableChild(labelField);

        // Start button
        ButtonWidget startButton = ButtonWidget.builder(Text.literal("Start"), (button) -> {
            if (!recording) {
                recording = true;
                recordedPositions.clear();
                sessionLabel = labelField.getText().isEmpty() ? "Recorded Session" : labelField.getText();
            }
        }).dimensions(centerX, startY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(startButton);

        // Stop button
        ButtonWidget stopButton = ButtonWidget.builder(Text.literal("Stop"), (button) -> {
            if (recording) {
                recording = false;
                saveToFile();
            }
        }).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build();
        this.addDrawableChild(stopButton);

        ButtonWidget nbtButton = ButtonWidget.builder(Text.literal("GetNBT"), (button) -> checkHeldItem()).dimensions(centerX, startY + buttonHeight + (spacing*3), buttonWidth, buttonHeight).build();
        this.addDrawableChild(nbtButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                "Position Recorder",
                this.width / 2,
                15,
                0xFFFFFF
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                "Session Label:",
                this.width / 2,
                30,
                0xA0A0A0
        );

        labelField.render(context, mouseX, mouseY, delta);

        String status = recording ? "Recording..." : "Not recording.";
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                status,
                this.width / 2,
                55,
                recording ? 0x00FF00 : 0xFF5555
        );
    }

    private static void registerTickEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (recording && mc.player != null) {
                Vec3d pos = mc.player.getPos();
                // Round to nearest integer
                int x = (int) Math.floor(pos.x);
                int y = (int) Math.floor(pos.y);
                int z = (int) Math.floor(pos.z);

                String formatted = String.format("%d, %d, %d", x, y, z);

                // Avoid duplicate consecutive entries
                if (recordedPositions.isEmpty() || !recordedPositions.getLast().equals(formatted)) {
                    recordedPositions.add(formatted);
                }
            }
        });
    }

    private static void saveToFile() {
        if (recordedPositions.isEmpty()) return;

        File file = new File(MinecraftClient.getInstance().runDirectory, "recorded_positions.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("=== " + sessionLabel + " ===\n");
            for (String entry : recordedPositions) {
                writer.write(entry);
                writer.newLine();
            }
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordedPositions.clear();
    }
    public static void checkHeldItem() {
        MinecraftClient client = MinecraftClient.getInstance();

        // Ensure player exists
        if (client.player == null) return;

        // Get the item in the player's main hand
        ItemStack heldItem = client.player.getMainHandStack();

        if (heldItem.isEmpty()) {
            ChatUtils.sendClientMessage("Player is not holding anything.");
            return;
        }

        // Print basic info
        File file = new File(MinecraftClient.getInstance().runDirectory, "recorded_items.txt");
        List<Text> nbt = heldItem.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("=== " + sessionLabel + " ===\n");
            writer.write(heldItem.getItem().toString());
            writer.write("=== Item metadata (NBT): ===\n");
            writer.write(nbt.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
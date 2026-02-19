package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Toggles.DungeonParticleTrail;
import net.dappls.legacy_utils.client.Toggles.SpiritParticleTrail;
import net.dappls.legacy_utils.client.Toggles.WaterParticleTrail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TogglesMenu extends AbstractLegacyGUI {

    public TogglesMenu() {
        super(Text.literal("Toggle Menu"));
    }

    @Override
    protected void setupGUI() {
        this.addLine("Welcome to the " + this.title.getString() + "!", 0xFFd0f4de);
        this.addLine(null);
        this.addLine("This menu has toggleable trails to help navigate through Lore areas!");
        this.addLine("The trails are NOT perfect, only providing general direction");


        // --- Button Setup (Moved from old init method) ---
        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 10;
        int centerX = (this.width - buttonWidth) / 2;
        // Anchor startY relative to the screen center, but adjusted slightly higher
        int startY = (this.height / 2) - (buttonHeight * 2);

        // 1. Water Button
        ButtonWidget waterButton = ButtonWidget.builder(
                Text.literal("Water: " + (WaterParticleTrail.isEnabled() ? "ON" : "OFF")),
                button -> {
                    WaterParticleTrail.toggleTrail();
                    button.setMessage(Text.literal("Water: " + (WaterParticleTrail.isEnabled() ? "ON" : "OFF")));
                }
        ).dimensions(centerX, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build();
        this.addDrawableChild(waterButton);

        // 2. Dungeon Button
        ButtonWidget dungeonButton = ButtonWidget.builder(
                Text.literal("Dungeon: " + getModeName(DungeonParticleTrail.getCurrentMode())), button -> {
                    DungeonParticleTrail.cycleMode(); // cycle to next mode
                    button.setMessage(Text.literal("Dungeon: " + getModeName(DungeonParticleTrail.getCurrentMode())));
                }).dimensions(centerX, startY + 2 * (buttonHeight + spacing), buttonWidth, buttonHeight).build();
        this.addDrawableChild(dungeonButton);

        // 3. Spirit Button
        ButtonWidget spiritButton = ButtonWidget.builder(
                Text.literal("Spirit: " + getSpiritModeName(SpiritParticleTrail.getCurrentMode())), button -> {
                    SpiritParticleTrail.cycleMode(); // cycle to next mode
                    button.setMessage(Text.literal("Spirit: " + getSpiritModeName(SpiritParticleTrail.getCurrentMode())));
                }).dimensions(centerX, startY + 3 * (buttonHeight + spacing), buttonWidth, buttonHeight).build();
        this.addDrawableChild(spiritButton);
    }

    // Helper methods remain in the class
    private String getModeName(DungeonParticleTrail.DungeonTrailMode mode) {
        return switch (mode) {
            case OFF -> "Disabled";
            case LAMP1 -> "Lamp 1";
            case LAMP2 -> "Lamp 2";
            case LAMP3 -> "Lamp 3";
            case WINDCHARGE -> "Wind Charge";
        };
    }

    private String getSpiritModeName(SpiritParticleTrail.SpiritTrailMode mode) {
        return switch (mode) {
            case OFF -> "disabled";
            case SEWER1 -> "sewer 1";
            case SEWER2 -> "sewer 2";
            case SEWER3 -> "sewer 3";
            case SEWER4 -> "sewer 4";
            case VALLEY1 -> "valley 1";
            case VALLEY2 -> "valley 2";
            case VALLEY2_5 -> "valley 2.5";
            case MINESHAFT2_5_1 -> "mineshaft 2.5-1";
            case MINESHAFT2_5_2 -> "mineshaft 2.5-2";
            case VALLEY3 -> "valley 3";
            case MINESHAFT3_1 -> "mineshaft 3-1";
            case MINESHAFT3_2 -> "mineshaft 3-2";
        };
    }

    // The init() and render() methods are now correctly omitted,
    // relying entirely on AbstractGUI's implementations.
}
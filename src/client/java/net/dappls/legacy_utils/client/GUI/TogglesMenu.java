package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Toggles.DungeonParticleTrail;
import net.dappls.legacy_utils.client.Toggles.SpiritParticleTrail;
import net.dappls.legacy_utils.client.Toggles.WaterParticleTrail;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TogglesMenu extends Screen {



    public TogglesMenu() {
        super(Text.literal("Toggle Menu"));
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
                "This menu has toggleable trails to help navigate through Lore areas!",
                this.width / 2, yStart+10, 0xFFAA00);

        context.drawCenteredTextWithShadow(this.textRenderer,
                "The trails are NOT perfect, only providing general direction",
                this.width / 2, yStart+20, 0xFFAA00);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidth = 150;
        int buttonHeight = 20;
        int centerX = (this.width - buttonWidth) / 2;
        int startY = (this.height - buttonHeight) / 2;

        ButtonWidget waterButton = ButtonWidget.builder(
                Text.literal("Water: " + (WaterParticleTrail.isEnabled() ? "ON" : "OFF")),
                button -> {
                    WaterParticleTrail.toggleTrail();
                    button.setMessage(Text.literal("Water: " + (WaterParticleTrail.isEnabled() ? "ON" : "OFF")));
                }
        ).dimensions(centerX, startY + buttonHeight + 10, buttonWidth, buttonHeight).build();
        this.addDrawableChild(waterButton);



        ButtonWidget dungeonButton = ButtonWidget.builder(
                Text.literal("Dungeon: " + getModeName(DungeonParticleTrail.getCurrentMode())), button -> {
                    DungeonParticleTrail.cycleMode(); // cycle to next mode
                    button.setMessage(Text.literal("Dungeon: " + getModeName(DungeonParticleTrail.getCurrentMode())));
                }).dimensions(centerX, startY + 2 * (buttonHeight + 10), buttonWidth, buttonHeight).build();
        this.addDrawableChild(dungeonButton);

        ButtonWidget spiritButton = ButtonWidget.builder(
                Text.literal("Spirit: " + getSpiritModeName(SpiritParticleTrail.getCurrentMode())), button -> {
                    SpiritParticleTrail.cycleMode(); // cycle to next mode
                    button.setMessage(Text.literal("Spirit: " + getSpiritModeName(SpiritParticleTrail.getCurrentMode())));
                }).dimensions(centerX, startY + 3 * (buttonHeight + 10), buttonWidth, buttonHeight).build();
        this.addDrawableChild(spiritButton);


        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"), button -> MinecraftClient.getInstance().setScreen(new ModMenu())).dimensions(10, 10, 20, 20).build();

        this.addDrawableChild(backButton);
    }
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

}

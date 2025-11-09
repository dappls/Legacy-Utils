package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.Toggles.DungeonParticleTrail;
import net.dappls.legacy_utils.Toggles.WaterParticleTrail;
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
                Text.literal("Water: " + getWaterModeName(WaterParticleTrail.getCurrentMode())),
                button -> {
                    WaterParticleTrail.cycleMode();
                    button.setMessage(Text.literal("Water: " + getWaterModeName(WaterParticleTrail.getCurrentMode())));
                }
        ).dimensions(centerX, startY + buttonHeight + 10, buttonWidth, buttonHeight).build();
        this.addDrawableChild(waterButton);


        ButtonWidget dungeonButton = ButtonWidget.builder(
                Text.literal("Dungeon: " + getModeName(DungeonParticleTrail.getCurrentMode())), button -> {
                    DungeonParticleTrail.cycleMode(); // cycle to next mode
                    button.setMessage(Text.literal("Dungeon: " + getModeName(DungeonParticleTrail.getCurrentMode())));
                }).dimensions(centerX, startY + 2 * (buttonHeight + 10), buttonWidth, buttonHeight).build();
        this.addDrawableChild(dungeonButton);


        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"), button -> MinecraftClient.getInstance().setScreen(new ModMenu())).dimensions(10, 10, 20, 20).build();

        this.addDrawableChild(backButton);
    }
    private String getModeName(DungeonParticleTrail.TrailMode mode) {
        return switch (mode) {
            case OFF -> "Disabled";
            case LAMPS1AND2 -> "Lamps1 & 2";
            case LAMP3 -> "Lamp 3";
            case WINDCHARGE -> "Wind Charge";
        };
    }
    private String getWaterModeName(WaterParticleTrail.TrailMode mode) {
        return switch (mode) {
            case OFF -> "Disabled";
            case PICKAXE -> "Pickaxe Path";
            case BOSS -> "Boss Path";
            case CONDUIT -> "Conduit Path";
        };
    }
}

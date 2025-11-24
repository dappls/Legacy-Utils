package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Util.PositionRecorderScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModMenu extends Screen {
    public ModMenu() {
        super(Text.literal("Mod Menu"));
    }

    @Override
    protected void init() {
        super.init();
        if (client != null) {

            int buttonWidth = 150;
            int buttonHeight = 20;
            int spacing = 8;
            int totalHeight = (7 * buttonHeight) + (6 * spacing);
            int startY = (this.height - totalHeight) / 2;
            int centerX = (this.width - buttonWidth) / 2;


            ButtonWidget puzzleInfoButton = ButtonWidget.builder(Text.literal("Puzzle Info"), (button) -> client.setScreen(new InfoMenu())).dimensions(centerX, startY, buttonWidth, buttonHeight).build();
            this.addDrawableChild(puzzleInfoButton);


            ButtonWidget ingenuityButton = ButtonWidget.builder(Text.literal("Ingenuity"), (button) -> client.setScreen(new IngenuityMenu())).dimensions(centerX, startY + (buttonHeight + spacing), buttonWidth, buttonHeight).build();
            this.addDrawableChild(ingenuityButton);


            ButtonWidget binaryButton = ButtonWidget.builder(Text.literal("Binary"), (button) -> client.setScreen(new BinaryModMenu())).dimensions(centerX, startY + (buttonHeight + spacing) * 2, buttonWidth, buttonHeight).build();
            this.addDrawableChild(binaryButton);


            ButtonWidget sevenBySevenButton = ButtonWidget.builder(Text.literal("7x7"), (button) -> client.setScreen(new SevenxSevenMenu())).dimensions(centerX, startY + (buttonHeight + spacing) * 3, buttonWidth, buttonHeight).build();
            this.addDrawableChild(sevenBySevenButton);


            ButtonWidget lampsButton = ButtonWidget.builder(Text.literal("Lamps"), (button) -> client.setScreen(new LightsOutMenu())).dimensions(centerX, startY + (buttonHeight + spacing) * 4, buttonWidth, buttonHeight).build();
            this.addDrawableChild(lampsButton);


            ButtonWidget honeyMazeButton = ButtonWidget.builder(Text.literal("Honey Maze"), (button) -> client.setScreen(new HoneyMenu())).dimensions(centerX, startY + (buttonHeight + spacing) * 5, buttonWidth, buttonHeight).build();
            this.addDrawableChild(honeyMazeButton);


            ButtonWidget togglesButton = ButtonWidget.builder(Text.literal("Toggles"), (button) -> client.setScreen(new TogglesMenu())).dimensions(centerX, startY + (buttonHeight + spacing) * 6, buttonWidth, buttonHeight).build();
            this.addDrawableChild(togglesButton);

           // if (client.player != null && "G12w".equals(client.player.getName().getString())) {
                ButtonWidget devButton = ButtonWidget.builder(Text.literal("DEV Button"), button -> client.setScreen(new PositionRecorderScreen())).dimensions(centerX, startY + (buttonHeight + spacing) * 7, buttonWidth, buttonHeight).build();
                this.addDrawableChild(devButton);
          //  }
        }
    }
}

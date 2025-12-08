package net.dappls.legacy_utils.client.GUI;

import net.dappls.legacy_utils.client.Util.PositionRecorderScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
// MODIFIED: Now extends AbstractGUI for consistency
public class ModMenu extends AbstractLegacyGUI {

    public ModMenu() {
        super(Text.literal("Mod Menu"));
    }

    // MODIFIED: Content moved from init() to setupGUI()
    @Override
    protected void setupGUI() {
        this.addLine("Welcome to the " + this.title.getString() + "!", 0xa5be00);
        this.addLine(null);
        // Since setupGUI is called within AbstractGUI's init, client will be non-null
        if (client == null) return;

        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 8;

        // Include the DEV button's space in totalHeight calculation for proper centering
        boolean isDev = "G12w".equals(client.player != null ? client.player.getName().getString() : "");
        int totalButtons = 7 + (isDev ? 1 : 0);
        int totalHeight = (totalButtons * buttonHeight) + ((totalButtons - 1) * spacing);

        int startY = (this.height - totalHeight) / 2;
        int centerX = (this.width - buttonWidth) / 2;
        int currentY = startY;

        // 1. Puzzle Info
        ButtonWidget puzzleInfoButton = ButtonWidget.builder(Text.literal("Puzzle Info"), (button) -> client.setScreen(new InfoMenu())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(puzzleInfoButton);
        currentY += buttonHeight + spacing;

        // 2. Ingenuity
        ButtonWidget ingenuityButton = ButtonWidget.builder(Text.literal("Ingenuity"), (button) -> client.setScreen(new IngenuityMenu())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(ingenuityButton);
        currentY += buttonHeight + spacing;

        // 3. Binary
        ButtonWidget binaryButton = ButtonWidget.builder(Text.literal("Binary"), (button) -> client.setScreen(new BinaryModMenu())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(binaryButton);
        currentY += buttonHeight + spacing;

        // 4. 7x7
        ButtonWidget sevenBySevenButton = ButtonWidget.builder(Text.literal("7x7"), (button) -> client.setScreen(new SevenxSevenMenu())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(sevenBySevenButton);
        currentY += buttonHeight + spacing;

        // 5. Lamps
        ButtonWidget lampsButton = ButtonWidget.builder(Text.literal("Lamps"), (button) -> client.setScreen(new LightsOutMenu())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(lampsButton);
        currentY += buttonHeight + spacing;

        // 6. Honey Maze
        ButtonWidget honeyMazeButton = ButtonWidget.builder(Text.literal("Honey Maze"), (button) -> client.setScreen(new HoneyMenu())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(honeyMazeButton);
        currentY += buttonHeight + spacing;

        // 7. Toggles
        ButtonWidget togglesButton = ButtonWidget.builder(Text.literal("Toggles"), (button) -> client.setScreen(new TogglesMenu())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
        this.addDrawableChild(togglesButton);
        currentY += buttonHeight + spacing;

        // 8. DEV Button (Conditional)
        if (isDev) {
            ButtonWidget devButton = ButtonWidget.builder(Text.literal("DEV Button"), button -> client.setScreen(new PositionRecorderScreen())).dimensions(centerX, currentY, buttonWidth, buttonHeight).build();
            this.addDrawableChild(devButton);
        }
    }

    // REMOVED: The render() and init() methods are omitted as they are handled by AbstractGUI.
}
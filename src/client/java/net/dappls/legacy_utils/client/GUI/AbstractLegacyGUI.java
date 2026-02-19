package net.dappls.legacy_utils.client.GUI;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLegacyGUI extends Screen {

    protected static final int LAVENDER = 0xFFE9EBF8;
    protected static final int GRANITE = 0xFFa69ca5; //

    private static final int TITLE_Y = 40;
    private static final int BODY_Y_START = 50;
    private static final int LINE_HEIGHT = 12;

        private record InfoLine(String text, int color) {
    }

    // --- MODIFIED: infoLines now stores the complex object ---
    private final List<InfoLine> infoLines = new ArrayList<>();

    public AbstractLegacyGUI(Text title) {
        super(title);
    }

    /**
     * MODIFIED: Adds a line with the default AMBER color.
     * Use addLine(String line, int color) for custom colors.
     */
    protected void addLine(String line) {
        // Null lines are added as a placeholder for spacing, using the default color
        this.infoLines.add(new InfoLine(line, LAVENDER));
    }

    /**
     * NEW: Adds a line with a specified custom color.
     * @param line The text content.
     * @param color The hex color (e.g., 0x00FF00) for this specific line.
     */
    protected void addLine(String line, int color) {
        this.infoLines.add(new InfoLine(line, color));
    }

    protected abstract void setupGUI();

    @Override
    protected void init() {
        super.init();
        this.infoLines.clear();

        this.setupGUI();


        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"),
                        b -> MinecraftClient.getInstance().setScreen(new ModMenu()))
                .dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);
    }

    /**
     * MODIFIED: This method no longer takes a color parameter.
     * It iterates over the list and uses the color stored with each line object.
     */
    protected void drawInfoLines(DrawContext context) {
        for (int i = 0; i < infoLines.size(); i++) {
            InfoLine lineData = infoLines.get(i);
            String line = lineData.text;

            // Check for null or empty string content
            if (line == null || line.isEmpty()) continue;


            int lineY = BODY_Y_START + ((i + 1) * LINE_HEIGHT);

            // Use the color stored in the InfoLine object
            context.drawCenteredTextWithShadow(this.textRenderer, line, this.width / 2, lineY, lineData.color);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
  //      this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, TITLE_Y, 0xFFFFFFFF);
        this.drawInfoLines(context);


    }
}
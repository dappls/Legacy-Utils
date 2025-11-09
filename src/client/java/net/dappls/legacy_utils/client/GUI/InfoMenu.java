package net.dappls.legacy_utils.client.GUI;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class InfoMenu extends Screen {
    public InfoMenu() {
        super(Text.literal("Puzzle Info"));
    }

    @Override
    protected void init() {
        super.init();

        // Back button
        ButtonWidget backButton = ButtonWidget.builder(Text.literal("<"),
                        (button) -> MinecraftClient.getInstance().setScreen(new ModMenu()))
                .dimensions(10, 10, 20, 20).build();
        this.addDrawableChild(backButton);

        // 101 Halls button
        ButtonWidget hallsButton = ButtonWidget.builder(Text.literal("101 Halls"),
                        (button) -> MinecraftClient.getInstance().setScreen(new Hallway101Screen(this)))
                .dimensions(this.width / 2 - 50, this.height / 2 - 10, 100, 20).build();
        this.addDrawableChild(hallsButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 40, 0xFFFFFF);

        int yStart = 70;
        int lineHeight = 12;

        context.drawCenteredTextWithShadow(this.textRenderer,
                "Welcome to the Ingenuity Puzzle Info Menu!", this.width / 2, yStart, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "This screen will contain information on Legacy puzzles", this.width / 2, yStart + lineHeight, 0xFFAA00);
        context.drawCenteredTextWithShadow(this.textRenderer,
                "Currently it's a WIP", this.width / 2, yStart + lineHeight * 2, 0xFFAA00);

        super.render(context, mouseX, mouseY, delta);
    }
}


@Environment(EnvType.CLIENT)
class Hallway101Screen extends Screen {
    private final Screen parent;
    private int currentPage = 0;

    private static final int ENTRIES_PER_PAGE = 25;

    protected Hallway101Screen(Screen parent) {
        super(Text.literal("101 Hallways - Admin:26257484"));
        this.parent = parent;
    }

    private static final String[] DOORS = new String[]{
            "0 - Door 101",
            "1 - Labs Building 01",
            "2 - Nothing",
            "3 - Nothing",
            "4 - Under Spawn",
            "5 - North castle’s basement",
            "6 - Cottage basement - (One Way)",
            "7 - Nothing",
            "8 - Dungeon entrance (One way)",
            "9 - Western ruins - (One Way)",
            "10 - South castle Admin room",
            "11 - Void ship",
            "12 - Door 54",
            "13 - Door 53",
            "14 - Door 54",
            "15 - Door 55",
            "16 - Halls Entrance Area",
            "17 - Door 57",
            "18 - Door 56",
            "19 - Door 59",
            "20 - Door 58",
            "21 - Door 61",
            "22 - Door 60",
            "23 - Door 63",
            "24 - Door 62",
            "25 - Door 65",
            "26 - Door 64",
            "27 - Door 67",
            "28 - Door 66",
            "29 - Door 69",
            "30 - Volcano isle of El’esh",
            "31 - Erynïteh's Spring Region",
            "32 - God Game (Includes “Ancient trial”)",
            "33 - Old FFA",
            "34 - Zone A (Includes “The tree house”)",
            "35 - Erynïteh's Winter Region",
            "36 - God Game Halloween",
            "37 - Beta FFA",
            "38 - KOTH Arena",
            "39 - Colosseum",
            "40 - Pine arena",
            "41 - Outpost",
            "42 - Grand sky islands",
            "43 - Bridge Minigame",
            "44 - Castle map",
            "45 - Desert Manor",
            "46 - Winter Realm but not winter",
            "47 - Extremely old FFA Spawn",
            "48 - Nothing",
            "49 - Old Versus",
            "50 - Nothing",
            "51 - Nothing",
            "52 - Door 12",
            "53 - Door 13",
            "54 - Door 14",
            "55 - Door 15",
            "56 - Door 16",
            "57 - Door 17",
            "58 - Door 18",
            "59 - Door 19",
            "60 - Door 20",
            "61 - Door 21",
            "62 - Door 22",
            "63 - Door 23",
            "64 - Door 24",
            "65 - Door 25",
            "66 - Door 26",
            "67 - Door 27",
            "68 - Door 28",
            "69 - Door 29",
            "70 - Lights Out/Gem deposit roof",
            "71 - Water Gem",
            "72 - Fire Gem",
            "73 - Menel Gem",
            "74 - Honey Gem",
            "75 - Himring Gem",
            "76 - Dungeon",
            "77 - Santirith",
            "78 - Corrupted Old Lights/out",
            "79 - Old Fire",
            "80 - Garden ~ (One Way)",
            "81 - Plane Above Void",
            "82 - 100k Event Area",
            "83 - Mini FFA (Includes “Metal-factory”)",
            "84 - Admin region",
            "85 - Nothing",
            "86 - Nothing",
            "87 - Nothing",
            "88 - Nothing",
            "89 - Nothing",
            "90 - Nothing",
            "91 - Nothing",
            "92 - Nothing",
            "93 - Nothing",
            "94 - Nothing",
            "95 - New Limbo (Includes “Nowhere”)",
            "96 - Nothing",
            "97 - Nothing",
            "98 - Nothing",
            "99 - Nothing",
            "100 - Nothing",
            "101 - Door 0"
    };

    @Override
    protected void init() {
        super.init();
        setupButtons();
    }

    private void setupButtons() {
        this.clearChildren();

        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"),
                        (button) -> MinecraftClient.getInstance().setScreen(parent))
                .dimensions(10, 10, 20, 20).build());

        // Prev page
        if (currentPage > 0) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("< Prev"),
                    (button) -> {
                        currentPage--;
                        setupButtons();
                    }).dimensions(this.width / 2 - 110, this.height - 30, 80, 20).build());
        }

        // Next page
        if ((currentPage + 1) * ENTRIES_PER_PAGE < DOORS.length) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Next >"),
                    (button) -> {
                        currentPage++;
                        setupButtons();
                    }).dimensions(this.width / 2 + 30, this.height - 30, 80, 20).build());
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);

        int yStart = 50;
        int lineHeight = 10;

        int startIndex = currentPage * ENTRIES_PER_PAGE;
        int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, DOORS.length);

        for (int i = startIndex; i < endIndex; i++) {
            int y = yStart + (i - startIndex) * lineHeight;
            context.drawTextWithShadow(this.textRenderer, DOORS[i], 20, y, 0xAAAAAA);
        }

        context.drawCenteredTextWithShadow(this.textRenderer,
                "Page " + (currentPage + 1) + " / " + ((DOORS.length + ENTRIES_PER_PAGE - 1) / ENTRIES_PER_PAGE),
                this.width / 2, this.height - 50, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}

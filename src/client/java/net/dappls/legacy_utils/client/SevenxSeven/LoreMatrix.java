package net.dappls.legacy_utils.client.SevenxSeven;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import java.util.List;
import java.util.Optional;

public class LoreMatrix {

    public static int[][] getHeldItemMatrix() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return null;

        ItemStack heldItem = client.player.getMainHandStack();
        // Get raw text lines directly
        List<Text> lines = heldItem.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC);

        // Usually: Line 0 = Name, Lines 1-7 = Grid
        if (lines.size() < 8) {
            System.out.println("Need 8 lines, found: " + lines.size());
            return null;
        }

        int[][] matrix = new int[7][7];
        for (int r = 0; r < 7; r++) {
            // We start at index 1 to skip the item name
            matrix[r] = parseLine(lines.get(r + 1));
        }
        return matrix;
    }

    private static int[] parseLine(Text line) {
        int[] row = new int[7];
        final int[] colIdx = {0};

        line.visit((style, content) -> {
            String text = content.trim();
            if (text.isEmpty()) return Optional.empty();

            // Check for Yellow (Case 5) via RGB color
            if (style.getColor() != null) {
                int rgb = style.getColor().getRgb();
                // Check common Yellow/Gold hex values
                if (rgb == 0xFFFF00 || rgb == 0xFFD700 || rgb == 0xFFF700) {
                    if (colIdx[0] < 7) row[colIdx[0]++] = 5;
                    return Optional.empty();
                }
            }

            // Check for Numbers (1-4)
            if (text.matches("\\d+")) {
                if (colIdx[0] < 7) row[colIdx[0]++] = Integer.parseInt(text);
            }
            // Handle placeholders like '█' if they represent empty cells
            else if (text.equals("█")) {
                if (colIdx[0] < 7) row[colIdx[0]++] = 0;
            }

            return Optional.empty();
        }, net.minecraft.text.Style.EMPTY);

        return row;
    }
}
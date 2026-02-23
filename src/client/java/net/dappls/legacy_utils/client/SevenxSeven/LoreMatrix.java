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
            // Remove all zero-width characters and whitespace
            String text = content.replaceAll("[\\u200B-\\u200D\\uFEFF\\s]", "");
            if (text.isEmpty()) return Optional.empty();

            // Check for Numbers (1-4) - check this FIRST before color checks
            // This handles cases like "‌1‌" after removing zero-width chars
            if (text.matches("\\d+")) {
                if (colIdx[0] < 7) row[colIdx[0]++] = Integer.parseInt(text);
                return Optional.empty();
            }

            // Handle placeholders like '█' - check for colored blocks
            if (text.equals("█")) {
                if (style.getColor() != null) {
                    int rgb = style.getColor().getRgb();

                    // Yellow/Gold blocks (Case 5)
                    if (rgb == 0xFFF75F || rgb == 0xFFFF00 || rgb == 0xFFD700 || rgb == 0xFFF700) {
                        if (colIdx[0] < 7) row[colIdx[0]++] = 5;
                        return Optional.empty();
                    }

                    // Cyan blocks (might be another value, or skip)
                    if (rgb == 0xBFFFFF) {
                        if (colIdx[0] < 7) row[colIdx[0]++] = 0; // Treat as empty or assign different value
                        return Optional.empty();
                    }

                    // Dark blocks (might be empty or different)
                    if (rgb == 0x222222) {
                        if (colIdx[0] < 7) row[colIdx[0]++] = 0;
                        return Optional.empty();
                    }
                }

                // Default gray/uncolored blocks are empty (0)
                if (colIdx[0] < 7) row[colIdx[0]++] = 0;
            }

            return Optional.empty();
        }, net.minecraft.text.Style.EMPTY);

        return row;
    }
}
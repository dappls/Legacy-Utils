package net.dappls.legacy_utils.client.SevenxSeven;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoreMatrix {

    // --- Invisibles / normalization ---
    private static String stripInvisibles(String s) {
        return s.replaceAll("[\\u200C\\u200B\\u200D\\uFEFF\\u2060-\\u2064\\u202A-\\u202E]", "");
    }

    private static String normalize(String s) {
        return stripInvisibles(s).trim();
    }

    // --- Row extraction (siblings=[ ... ]] ) ---
    private static List<String> extractRows(String lore) {
        String clean = stripInvisibles(lore);
        Pattern p = Pattern.compile("siblings=\\[(.*?)]]", Pattern.DOTALL);
        Matcher m = p.matcher(clean);
        List<String> rows = new ArrayList<>();
        while (m.find()) {
            rows.add(m.group(1));
        }
        return rows;
    }

    // --- Literal + style extraction ---
    private static List<String[]> extractLiterals(String siblings) {
        String clean = stripInvisibles(siblings);
        Pattern p = Pattern.compile("literal\\{(.*?)}(?:\\[style=\\{([^}]*)}])?");
    Matcher m = p.matcher(clean);
    List<String[]> lits = new ArrayList<>();
        while (m.find()) {
        // Preserve spaces, only strip invisibles
        String text = stripInvisibles(m.group(1));
        String style = m.group(2) != null ? m.group(2) : null;
        lits.add(new String[]{text, style});
    }
        return lits;
}

// --- Style parsing into a map ---
private static Map<String, String> parseStyle(String styleRaw) {
    Map<String, String> map = new HashMap<>();
    if (styleRaw == null) return map;

    String s = stripInvisibles(styleRaw);
    for (String part : s.split("\\s*,\\s*")) {
        String trimmed = part.trim();
        if (trimmed.isEmpty()) continue;

        if (trimmed.startsWith("!")) {
            String key = trimmed.substring(1).toLowerCase(Locale.ROOT);
            map.put(key, "false");
            continue;
        }

        int eq = trimmed.indexOf('=');
        if (eq > 0) {
            String key = trimmed.substring(0, eq).toLowerCase(Locale.ROOT).trim();
            String val = trimmed.substring(eq + 1).toLowerCase(Locale.ROOT).trim();
            map.put(key, val);
        } else {
            map.put(trimmed.toLowerCase(Locale.ROOT), "true");
        }
    }
    return map;
}

// --- Color helpers ---
private static final Set<String> YELLOW_TOKENS = new HashSet<>(Arrays.asList(
        "yellow", "#ffff00", "#fff700", "#fff75f", "#fffa65", "#ffd700"
));

private static boolean isYellow(Map<String, String> styleMap) {
    String color = styleMap.get("color");
    if (color == null) return false;
    color = color.trim().toLowerCase(Locale.ROOT);
    if (color.startsWith("#") && color.length() == 4) {
        char r = color.charAt(1), g = color.charAt(2), b = color.charAt(3);
        color = "#" + ("" + r + r + g + g + b + b);
    }
    return YELLOW_TOKENS.contains(color);
}

// --- Helpers ---
private static boolean isSpaceLiteral(String text) {
    return text != null && stripInvisibles(text).equals(" ");
}

// --- Value mapping ---
private static int toCell(String text, String styleRaw) {
    Map<String, String> styleMap = parseStyle(styleRaw);
    String t = normalize(text);
    if (isYellow(styleMap)) {
        return 5;
    }
    if (t.matches("\\d+")) {
        try {
            return Integer.parseInt(t);
        } catch (NumberFormatException ignored) {}
    }

    return 0;
}


// --- Matrix builder ---
public static int[][] buildMatrix(String lore) {
    List<String> rows = extractRows(lore);
    if (rows.size() < 8) {
        System.out.println("Need at least 8 rows, found " + rows.size());
        return null;
    }
    rows = rows.subList(1, 8);

    int[][] matrix = new int[7][7];
    for (int r = 0; r < 7; r++) {
        List<String[]> lits = extractLiterals(rows.get(r));
        List<Integer> cells = new ArrayList<>(7);

        for (String[] lit : lits) {
            String rawText = lit[0];
            String styleRaw = lit[1];

            if (isSpaceLiteral(rawText)) {
                continue; // skip styled spaces
            }

            boolean isBlock = "█".equals(normalize(rawText));
            boolean isColored = styleRaw != null && parseStyle(styleRaw).containsKey("color");

            int val = toCell(rawText, styleRaw);

            if (val != 0 || isBlock || isColored) {
                cells.add(val);
            }
            if (cells.size() == 7) break;
        }

        while (cells.size() < 7) cells.add(0);
        for (int c = 0; c < 7; c++) {
            matrix[r][c] = cells.get(c);
        }
    }
    return matrix;
}

public static int[][] getHeldItemMatrix() {
    MinecraftClient client = MinecraftClient.getInstance();
    assert client.player != null;
    ItemStack heldItem = client.player.getMainHandStack();
    String lore = heldItem.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC).toString();
    return buildMatrix(lore);
}
}

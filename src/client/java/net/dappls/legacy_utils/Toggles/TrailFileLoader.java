package net.dappls.legacy_utils.Toggles;

import net.minecraft.util.math.BlockPos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TrailFileLoader {

    private static String BASE_PATH = ""; // inside src/main/resources/
    public static void SetPath(String path) {
        BASE_PATH = path;
    }
    /**
     * Load a trail file from resources and return a list of BlockPos
     * @param filename Example: "sewer1.txt"
     * @return List<BlockPos> with all points in the file
     */
    public static List<BlockPos> load(String filename) {
        List<BlockPos> trail = new ArrayList<>();
        String path = BASE_PATH + filename;

        try (InputStream stream = TrailFileLoader.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                System.err.println("Trail file not found in resources: " + path);
                return trail;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#") || line.startsWith("=")) continue;

                    String[] parts = line.split("[,\\s]+");
                    if (parts.length != 3) continue;

                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);
                    trail.add(new BlockPos(x, y, z));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return trail;
    }
}

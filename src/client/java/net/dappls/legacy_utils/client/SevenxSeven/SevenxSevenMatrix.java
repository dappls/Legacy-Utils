package net.dappls.legacy_utils.client.SevenxSeven;

public class SevenxSevenMatrix {
    public static int[][] matrix7x7 = new int[7][7];

    public static void rotateMatrix90Clockwise() {
        int n = 7;
        int[][] rotated = new int[n][n];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                // Standard 90-degree clockwise formula
                rotated[c][n - 1 - r] = matrix7x7[r][c];
            }
        }
        matrix7x7 = rotated;
    }
}
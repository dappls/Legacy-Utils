package net.dappls.legacy_utils.client.SevenxSeven;

public class SevenxSevenMatrix {
    public static int[][] matrix7x7 = {
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0 }
    };
    public static void rotateMatrix90Clockwise() {
        int n = matrix7x7.length;

        for (int i = 0; i < n / 2; i++) {
            int[] temp = matrix7x7[i];
            matrix7x7[i] = matrix7x7[n - 1 - i];
            matrix7x7[n - 1 - i] = temp;
        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix7x7[i][j];
                matrix7x7[i][j] = matrix7x7[j][i];
                matrix7x7[j][i] = temp;
            }
        }
    }
}

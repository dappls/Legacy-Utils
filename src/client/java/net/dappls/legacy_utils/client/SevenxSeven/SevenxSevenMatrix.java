package net.dappls.legacy_utils.client.SevenxSeven;

public class SevenxSevenMatrix {
    public static int[][] matrix7x7 = {
            { 1,  2,  3,  4,  5,  6,  7 },
            { 8,  9,  10, 11, 12, 13, 14 },
            { 15, 16, 17, 18, 19, 20, 21 },
            { 22, 23, 24, 25, 26, 27, 28 },
            { 29, 30, 31, 32, 33, 34, 35 },
            { 36, 37, 38, 39, 40, 41, 42 },
            { 43, 44, 45, 46, 47, 48, 49 }
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

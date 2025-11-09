package net.dappls.legacy_utils.client.LightsOut;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GaussianElimination {

    /**
     * Solves A * x = b over GF(2) (binary field).
     * Returns one valid binary solution vector (0 or 1).
     * Throws IllegalStateException if no valid solution exists.
     */
    public int[] solve(int[][] A, int[] b) {
        if (A == null || b == null) throw new IllegalArgumentException("Matrix A or vector b cannot be null.");
        int n = A.length;
        if (n == 0 || A[0].length != n) throw new IllegalArgumentException("Matrix A must be square and non-empty.");
        if (b.length != n) throw new IllegalArgumentException("Vector b must have same length as A.");

        // Build augmented matrix [A | b]
        int[][] mat = new int[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, mat[i], 0, n);
            mat[i][n] = b[i] & 1; // force binary
        }

        int row = 0;
        // --- Forward elimination ---
        for (int col = 0; col < n && row < n; col++) {
            // Find pivot row
            int pivot = -1;
            for (int i = row; i < n; i++) {
                if (mat[i][col] == 1) {
                    pivot = i;
                    break;
                }
            }
            if (pivot == -1) continue; // no pivot in this column

            // Swap to top
            if (pivot != row) {
                int[] temp = mat[row];
                mat[row] = mat[pivot];
                mat[pivot] = temp;
            }

            // Eliminate all below
            for (int i = row + 1; i < n; i++) {
                if (mat[i][col] == 1) {
                    for (int j = col; j <= n; j++) {
                        mat[i][j] ^= mat[row][j];
                    }
                }
            }
            row++;
        }

        // --- Backward elimination (RREF) ---
        for (int i = n - 1; i >= 0; i--) {
            // Find leading 1 in row
            int lead = -1;
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 1) {
                    lead = j;
                    break;
                }
            }
            if (lead == -1) continue; // skip empty rows

            // Eliminate all above
            for (int k = 0; k < i; k++) {
                if (mat[k][lead] == 1) {
                    for (int j = lead; j <= n; j++) {
                        mat[k][j] ^= mat[i][j];
                    }
                }
            }
        }

        // --- Check consistency ---
        for (int i = 0; i < n; i++) {
            boolean allZero = true;
            for (int j = 0; j < n; j++) {
                if (mat[i][j] != 0) {
                    allZero = false;
                    break;
                }
            }
            if (allZero && mat[i][n] == 1) {
                throw new IllegalStateException("No valid solution exists (inconsistent system).");
            }
        }

        // --- Extract solution ---
        int[] x = new int[n];
        for (int i = 0; i < n; i++) {
            int lead = -1;
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 1) {
                    lead = j;
                    break;
                }
            }
            if (lead != -1) {
                x[lead] = mat[i][n];
            }
        }
        return x;
    }
}

package net.dappls.legacy_utils.client.LightsOut;



import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class BoardMatrix {

    private ArrayList<Integer> boardState = new ArrayList<>();
    private final int[][] toggleMatrixOps;
    private final List<BlockPos> lampsPos;

    public BoardMatrix() {
        lampsPos = shiftBlockPositions(new ArrayList<>(List.of(
                new BlockPos(-11259, 34, 12683), // LIGHTS_OUT_COORD_1
                new BlockPos(-11259, 34, 12669), // LIGHTS_OUT_COORD_2
                new BlockPos(-11259, 34, 12655), // LIGHTS_OUT_COORD_3

                new BlockPos(-11252, 34, 12690), // LIGHTS_OUT_COORD_4
                new BlockPos(-11252, 34, 12683), // LIGHTS_OUT_COORD_5
                new BlockPos(-11252, 34, 12676), // LIGHTS_OUT_COORD_6
                new BlockPos(-11252, 34, 12669), // LIGHTS_OUT_COORD_7
                new BlockPos(-11252, 34, 12662), // LIGHTS_OUT_COORD_8
                new BlockPos(-11252, 34, 12655), // LIGHTS_OUT_COORD_9
                new BlockPos(-11252, 34, 12648), // LIGHTS_OUT_COORD_10

                new BlockPos(-11245, 34, 12683), // LIGHTS_OUT_COORD_11
                new BlockPos(-11245, 34, 12676), // LIGHTS_OUT_COORD_12
                new BlockPos(-11245, 34, 12669), // LIGHTS_OUT_COORD_13
                new BlockPos(-11245, 34, 12662), // LIGHTS_OUT_COORD_14

                new BlockPos(-11238, 34, 12690), // LIGHTS_OUT_COORD_15
                new BlockPos(-11238, 34, 12683), // LIGHTS_OUT_COORD_16
                new BlockPos(-11238, 34, 12676), // LIGHTS_OUT_COORD_17
                new BlockPos(-11238, 35, 12669), // LIGHTS_OUT_COORD_18 (center)
                new BlockPos(-11238, 34, 12662), // LIGHTS_OUT_COORD_19
                new BlockPos(-11238, 34, 12655), // LIGHTS_OUT_COORD_20
                new BlockPos(-11238, 34, 12648), // LIGHTS_OUT_COORD_21

                new BlockPos(-11231, 34, 12676), // LIGHTS_OUT_COORD_22
                new BlockPos(-11231, 34, 12669), // LIGHTS_OUT_COORD_23
                new BlockPos(-11231, 34, 12662), // LIGHTS_OUT_COORD_24
                new BlockPos(-11231, 34, 12655), // LIGHTS_OUT_COORD_25

                new BlockPos(-11224, 34, 12690), // LIGHTS_OUT_COORD_26
                new BlockPos(-11224, 34, 12683), // LIGHTS_OUT_COORD_27
                new BlockPos(-11224, 34, 12676), // LIGHTS_OUT_COORD_28
                new BlockPos(-11224, 34, 12669), // LIGHTS_OUT_COORD_29
                new BlockPos(-11224, 34, 12662), // LIGHTS_OUT_COORD_30
                new BlockPos(-11224, 34, 12655), // LIGHTS_OUT_COORD_31
                new BlockPos(-11224, 34, 12648), // LIGHTS_OUT_COORD_32

                new BlockPos(-11217, 34, 12683), // LIGHTS_OUT_COORD_33
                new BlockPos(-11217, 34, 12669), // LIGHTS_OUT_COORD_34
                new BlockPos(-11217, 34, 12655)  // LIGHTS_OUT_COORD_35
        )));

        toggleMatrixOps = defineToggleMatrix();
    }
    public static List<BlockPos> shiftBlockPositions(List<BlockPos> positions) {
        List<BlockPos> shifted = new ArrayList<>();
        for (BlockPos pos : positions) {
            shifted.add(pos.add(1,0,0));
        }
        return shifted;
    }


    public boolean isPlayerAtBoard() {
        if (lampsPos == null || lampsPos.isEmpty()) return false;

        BlockPos center = lampsPos.get(17);
        Vec3d playerPos = MinecraftClient.getInstance().player.getPos();

        if (playerPos.distanceTo(new Vec3d(center.getX(), center.getY(), center.getZ())) > 40.0) {
            MinecraftClient.getInstance().player.sendMessage(
                    Text.literal("You are not near the lights board, go to the center."),
                    false
            );
            return false;
        }
        return true;
    }

    /** Reads the current lamp state from the world (1 = lit, 0 = unlit). */
    public ArrayList<Integer> fetchBoardState() {
        ArrayList<Integer> state = new ArrayList<>();
        if (MinecraftClient.getInstance().world == null) return state;
        for (BlockPos pos : lampsPos) {
            boolean lit = MinecraftClient.getInstance().world.getBlockState(pos).isOf(Blocks.REDSTONE_LAMP) && MinecraftClient.getInstance().world.getBlockState(pos).get(Properties.LIT);
            state.add(lit ? 1 : 0);
        }
        return state;
    }

    public void updateBoard() {
        this.boardState = fetchBoardState();
    }

    public void init() {
        updateBoard();
    }

    private int[][] defineToggleMatrix() {

        return new int[][]{
                {1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {1,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,1,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,1,0,0,0,0,0,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,1,0,0,0,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,1,0,0,0,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,0,0,0,0,1,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,0,0,0,0,1,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,1,0,0,0,0,1,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,1,0,0,0,1,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,1,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,1,1,0,0,1},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1}
        };
    }





    public int[] solve(int target) {
        updateBoard();
        int N = boardState.size();
        int[] currentState = boardState.stream().mapToInt(Integer::intValue).toArray();

        // Build target state vector
        int[] desiredState = new int[N];
        switch (target) {
            case 0 -> {
                // All off
            }
            case 1 -> // All on
                    Arrays.fill(desiredState, 1);
            case 2 -> {
                // Center lit only (lamp 18, index 17)
                desiredState[17] = 1;
            }
            default -> throw new IllegalArgumentException("Invalid target type: " + target);
        }

        // Compute b = current XOR desired (A * x = b mod 2)
        int[] b = new int[N];
        for (int i = 0; i < N; i++) {
            b[i] = currentState[i] ^ desiredState[i];
        }

        // Solve using Gaussian elimination
        return new GaussianElimination().solve(toggleMatrixOps, b);
    }


    /** Clears and then renders the solution plates. */
    public void renderSolution(int target) {
        int[] solution = solve(target);
        RenderPlates.clearAll(lampsPos);
        RenderPlates.render(lampsPos, solution);
        MinecraftClient.getInstance().player.closeScreen();
    }
}

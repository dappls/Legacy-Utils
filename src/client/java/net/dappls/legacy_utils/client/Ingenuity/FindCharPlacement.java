package net.dappls.legacy_utils.client.Ingenuity;


import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FindCharPlacement {
    static final List<List<Boolean>> TWO_LODESTONE_PATTERNS = Arrays.asList(
            Arrays.asList(false, true,  false, true),   // N + S  → type 1
            Arrays.asList(true,  false, false, true),   // W + S  → type 2
            Arrays.asList(true,  false, true,  false),  // W + E  → type 3
            Arrays.asList(false, false, true,  true),   // E + S  → type 4
            Arrays.asList(true,  true,  false, false),  // W + N  → type 5
            Arrays.asList(false, true,  true,  false)   // N + E  → type 6

    );

    static final int[][] diamond = {
            {-1, 0, 0}, {0, 0, -1}, {1, 0, 0}, {0, 0, 1}
    };

    static final int[][] positions = {
            {-12210, 71, 12579}, {-12204, 71, 12573}, {-12198, 71, 12579}, {-12204, 71, 12585},
            {-12162, 71, 12579}, {-12156, 71, 12573}, {-12150, 71, 12579}, {-12156, 71, 12585}
    };
    private static boolean isLodestoneAt(World world, BlockPos pos) {
        // Only check if the chunk is loaded
        BlockState b = world.getBlockState(pos);
        return b.isOf(Blocks.LODESTONE);

    }


    public static List<Integer> getStartingValues(World world) {
        List<Integer> points = new ArrayList<>();

        // Wrap positions in a list so we can get an iterator

        // Iterate through all positions
        // current element of positions
        for (int[] pos : positions) {
            List<Boolean> table = new ArrayList<>();
            for (int[] offset : diamond) {
                BlockPos opos = new BlockPos(
                        pos[0] + offset[0],
                        pos[1] + offset[1],
                        pos[2] + offset[2]
                );

                table.add(isLodestoneAt(world, opos));
//                ChatUtils.sendClientMessage(table.toString());
//                ChatUtils.sendClientMessage(opos.toString());
            }

            int type = TWO_LODESTONE_PATTERNS.indexOf(table);
            points.add(type + 1);
//            ChatUtils.sendClientMessage(String.valueOf(type + 1));
        }

//        ChatUtils.sendClientMessage(String.valueOf(points));


        return new ArrayList<>(points);
    }

}



//        // Stores the final list of "types" (integer values for each position)
//        List<Integer> points = new ArrayList<>();
//
//        // Loop through each predefined position in the puzzle
//        for (int[] pos : positions) {
//            // For each position, build a pattern of booleans representing lodestone presence
//            List<Boolean> table = new ArrayList<>();
//
//            // Check all 4 directions around the position (defined in "diamond")
//            for (int[] offset : diamond) {
//                // Create a BlockPos offset from the current position
//                BlockPos opos = new BlockPos(pos[0] + offset[0], pos[1] + offset[1], pos[2] + offset[2]);
//
//                // Check if there is a lodestone at this offset position
//                table.add(dappls.ingenuity.BlockChecker.isLodestoneAt(world, opos));
//
//                // Debug: print out the boolean table so far
//                ChatUtils.sendClientMessage(table.toString());
//            }
//
//            // Compare the pattern (N, W, E, S lodestones) against the known valid patterns
//            int type = TWO_LODESTONE_PATTERNS.indexOf(table);
//
//            // Store the "type" (shifted by +1 so it's 1–6 instead of 0–5)
//            points.add(type + 1);
//
//            // Debug: print which type was matched
//            ChatUtils.sendClientMessage(String.valueOf(type + 1));
//        }



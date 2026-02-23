package net.dappls.legacy_utils.client.Ithil;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public class IthilSolver {

    // ALL correct plate positions (flattened — no rooms, no logic)
    private static final BlockPos[] CORRECT_PLATES = {
            // Room 0
            new BlockPos(-11225, 32, 12740),
            new BlockPos(-11219, 32, 12746),
            new BlockPos(-11225, 32, 12724),
            new BlockPos(-11219, 32, 12724),
            new BlockPos(-11219, 32, 12730),
            new BlockPos(-11203, 32, 12724),
            new BlockPos(-11203, 32, 12730),
            new BlockPos(-11209, 32, 12746),

            // Room 1
            new BlockPos(-11225, 32, 12778),
            new BlockPos(-11209, 32, 12772),
            new BlockPos(-11203, 32, 12778),
            new BlockPos(-11203, 32, 12788),
            new BlockPos(-11203, 32, 12794),
            new BlockPos(-11209, 32, 12794),
            new BlockPos(-11209, 32, 12788),
            new BlockPos(-11225, 32, 12794),

            // Room 2
            new BlockPos(-11251, 32, 12772),
            new BlockPos(-11257, 32, 12778),
            new BlockPos(-11251, 32, 12794),
            new BlockPos(-11267, 32, 12788),
            new BlockPos(-11273, 32, 12788),
            new BlockPos(-11267, 32, 12778),
            new BlockPos(-11273, 32, 12778),
            new BlockPos(-11273, 32, 12772),

            // Room 3
            new BlockPos(-11257, 32, 12740),
            new BlockPos(-11251, 32, 12746),
            new BlockPos(-11273, 32, 12746),
            new BlockPos(-11267, 32, 12740),
            new BlockPos(-11267, 32, 12724),
            new BlockPos(-11267, 32, 12730),
            new BlockPos(-11257, 32, 12724),
            new BlockPos(-11251, 32, 12724)
    };

    /** Call this once to visually generate golden plates */
    public static void showGoldenPlates() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        if (world == null) return;

        BlockState goldPlate = Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState();

        for (BlockPos pos : CORRECT_PLATES) {
            world.setBlockState(pos, goldPlate, 0);
        }
    }
}
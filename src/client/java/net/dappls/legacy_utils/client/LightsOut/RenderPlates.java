package net.dappls.legacy_utils.client.LightsOut;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.List;

@Environment(EnvType.CLIENT)
public class RenderPlates {

    /** Places plates exactly above each lamp according to the solution vector. */
    public static void render(List<BlockPos> lampPositions, int[] solution) {
        if (MinecraftClient.getInstance().world == null) return;

        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                BlockPos platePos = lampPositions.get(i).up().west();
                MinecraftClient.getInstance().world.setBlockState(platePos, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState());
            }
        }
    }

    /** Clears all pressure plates above the lamps. */
    public static void clearAll(List<BlockPos> lampPositions) {
        if (MinecraftClient.getInstance().world == null) return;

        for (BlockPos pos : lampPositions) {
            MinecraftClient.getInstance().world.setBlockState(pos.up().west(), Blocks.AIR.getDefaultState());
        }

    }
}


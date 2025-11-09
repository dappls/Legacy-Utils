package net.dappls.legacy_utils.client.Honey;

import net.minecraft.block.CandleBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

public class CandleChecker {

    // --- Pressure plates and their corresponding candles ---
    public static final List<BlockPos> PRESSURE_PLATES = Arrays.asList(
            new BlockPos(9763, 55, 52356),
            new BlockPos(9797, 55, 52356),
            new BlockPos(9811, 55, 52328), //doesnt work?
            new BlockPos(9795, 55, 52300),
            new BlockPos(9755, 55, 52298),
            new BlockPos(9741, 55, 52326)
    );

    public static final List<BlockPos> CANDLES = Arrays.asList(
            new BlockPos(9763, 56, 52355),
            new BlockPos(9798, 56, 52356),
            new BlockPos(9810, 56, 52328), //doesnt work?
            new BlockPos(9795, 56, 52301),
            new BlockPos(9756, 56, 52298),
            new BlockPos(9741, 56, 52327)
    );

    /**
     * Returns the position of the pressure plate that corresponds to a lit candle.
     * If no candle is lit, returns null.
     */
    public static BlockPos getActivePressurePlate() {
        if (MinecraftClient.getInstance().world == null) return null;

        for (int i = 0; i < CANDLES.size(); i++) {
            BlockPos candlePos = CANDLES.get(i);

            var state = MinecraftClient.getInstance().world.getBlockState(candlePos);
            if (state.getBlock() instanceof CandleBlock && state.get(Properties.LIT)) {
                return PRESSURE_PLATES.get(i);
            }
        }

        return null; // No lit candle found
    }
}

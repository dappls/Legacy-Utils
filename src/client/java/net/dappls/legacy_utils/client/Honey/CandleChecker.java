package net.dappls.legacy_utils.client.Honey;

import net.minecraft.block.CandleBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CandleChecker {

    // Pressure plates and matching candles by index
    public static final List<BlockPos> PRESSURE_PLATES = Arrays.asList(
            new BlockPos(9763, 55, 52356),
            new BlockPos(9797, 55, 52356),
            new BlockPos(9811, 55, 52328),
            new BlockPos(9795, 55, 52300),
            new BlockPos(9755, 55, 52298),
            new BlockPos(9741, 55, 52326)
    );

    public static final List<BlockPos> CANDLES = Arrays.asList(
            new BlockPos(9763, 56, 52355),
            new BlockPos(9798, 56, 52356),
            new BlockPos(9810, 56, 52328),
            new BlockPos(9795, 56, 52301),
            new BlockPos(9756, 56, 52298),
            new BlockPos(9741, 56, 52327)
    );

    /**
     * Returns a list of ONLY the pressure plates whose candles are lit.
     * The list is ordered according to the indices in CANDLES/PRESSURE_PLATES.
     * Returns an empty list if world is null or no candles are lit.
     */
    public static List<BlockPos> getActivePressurePlates() {
        if (MinecraftClient.getInstance().world == null) return List.of();

        List<BlockPos> litPlates = new ArrayList<>();

        for (int i = 0; i < CANDLES.size(); i++) {
            BlockPos candlePos = CANDLES.get(i);

            var state = MinecraftClient.getInstance().world.getBlockState(candlePos);
            if (state.getBlock() instanceof CandleBlock && Boolean.TRUE.equals(state.get(Properties.LIT))) {
                litPlates.add(PRESSURE_PLATES.get(i));
            }
        }

        return litPlates;
    }
}
